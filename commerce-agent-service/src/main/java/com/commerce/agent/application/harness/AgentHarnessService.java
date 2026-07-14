package com.commerce.agent.application.harness;

import com.commerce.agent.application.model.ChatSession;
import com.commerce.agent.application.service.AgentRoutingService;
import com.commerce.agent.application.service.ChatAgentService;
import com.commerce.agent.application.service.SessionService;
import com.commerce.agent.application.service.SupervisorWorkflowService;
import com.commerce.agent.application.service.UserContextService;
import com.commerce.agent.config.AgentProperties;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AgentHarnessService {

	private final AgentProperties properties;

	private final AgentRoutingService routingService;

	private final ChatAgentService chatAgentService;

	private final SupervisorWorkflowService workflowService;

	private final SessionService sessionService;

	private final UserContextService userContextService;

	private final AgentRunRegistry runRegistry;

	private final HarnessToolExecutor toolExecutor;

	private final ExecutorService executor;

	public AgentHarnessService(AgentProperties properties, AgentRoutingService routingService,
			ChatAgentService chatAgentService, SupervisorWorkflowService workflowService, SessionService sessionService,
			UserContextService userContextService, AgentRunRegistry runRegistry, HarnessToolExecutor toolExecutor) {
		this.properties = properties;
		this.routingService = routingService;
		this.chatAgentService = chatAgentService;
		this.workflowService = workflowService;
		this.sessionService = sessionService;
		this.userContextService = userContextService;
		this.runRegistry = runRegistry;
		this.toolExecutor = toolExecutor;
		this.executor = Executors.newFixedThreadPool(Math.max(1, properties.getHarness().getMaxConcurrentRuns()),
				namedThreadFactory());
	}

	public AgentHarnessResult run(AgentRunCommand command) {
		if (!StringUtils.hasText(command.question())) {
			throw new IllegalArgumentException("question must not be blank");
		}
		ChatSession session = sessionService.getOrCreate(command.requestedSessionId(), command.userContext().userId());
		AgentRun run = runRegistry.create(session.id(), command.userContext().userId(), command.question(),
				command.eventConsumer());
		Future<?> task = executor.submit(() -> execute(run, session, command));
		run.attach(task);
		try {
			task.get(Math.max(1, properties.getHarness().getMaxDurationMs()), TimeUnit.MILLISECONDS);
		} catch (TimeoutException error) {
			run.timeOut();
		} catch (CancellationException ignored) {
			// The cancel/timeout path has already assigned the terminal run status.
		} catch (InterruptedException error) {
			Thread.currentThread().interrupt();
			run.cancel("等待 Agent 运行结果时被中断");
		} catch (ExecutionException error) {
			run.fail(error.getCause() == null ? error : error.getCause());
		}
		return run.result();
	}

	@PreDestroy
	public void shutdown() {
		executor.shutdownNow();
	}

	private void execute(AgentRun run, ChatSession session, AgentRunCommand command) {
		userContextService.set(command.userContext());
		try {
			run.start();
			long lockStartedAt = System.nanoTime();
			try (SessionService.SessionLease ignored = sessionService.acquire(session.id(), command.userContext().userId(),
					properties.getHarness().getSessionLockTimeoutMs())) {
				run.sessionLocked(elapsedMs(lockStartedAt));
				executeLocked(run, session, command);
			}
		} catch (AgentRunAbortedException error) {
			if (error.getMessage() != null && error.getMessage().contains("时间预算")) {
				run.timeOut();
			} else {
				run.cancel(error.getMessage() == null ? "Agent 运行已取消" : error.getMessage());
			}
		} catch (RuntimeException error) {
			run.fail(error);
		} finally {
			userContextService.clear();
		}
	}

	private void executeLocked(AgentRun run, ChatSession session, AgentRunCommand command) {
		AgentRoutingService.AgentRoute route = command.requestedMode() == null
				? routingService.route(command.question())
				: new AgentRoutingService.AgentRoute(command.requestedMode(), "API 指定执行模式");
		run.routed(route.mode().name(), route.reason());
		run.modelStarted();
		long modelStartedAt = System.nanoTime();
		String answer;
		try (HarnessToolExecutor.Scope ignored = toolExecutor.open(run)) {
			if (route.isBusinessWorkflow()) {
				answer = workflowService.analyze(command.question());
				publishCompleteAnswer(run, command, answer);
			} else if (command.streaming()) {
				answer = chatAgentService.stream(command.question(), session.history(),
						chunk -> publishChunk(run, command, chunk));
			} else {
				answer = chatAgentService.chat(command.question(), session.history());
				run.addOutputChars(answer == null ? 0 : answer.length());
			}
		}
		if (!StringUtils.hasText(answer)) {
			throw new IllegalStateException("Agent 未返回有效内容");
		}
		run.modelCompleted(elapsedMs(modelStartedAt));
		run.succeed(answer);
		session.addExchange(command.question(), answer);
	}

	private void publishCompleteAnswer(AgentRun run, AgentRunCommand command, String answer) {
		run.addOutputChars(answer == null ? 0 : answer.length());
		if (!command.streaming() || !StringUtils.hasText(answer)) {
			return;
		}
		for (String chunk : answer.split("(?<=\\n)")) {
			if (StringUtils.hasText(chunk)) {
				command.outputConsumer().accept(chunk);
			}
		}
	}

	private void publishChunk(AgentRun run, AgentRunCommand command, String chunk) {
		if (!StringUtils.hasText(chunk)) {
			return;
		}
		run.addOutputChars(chunk.length());
		command.outputConsumer().accept(chunk);
	}

	private long elapsedMs(long startedAt) {
		return Math.max(0, (System.nanoTime() - startedAt) / 1_000_000);
	}

	private ThreadFactory namedThreadFactory() {
		AtomicInteger sequence = new AtomicInteger();
		return runnable -> {
			Thread thread = new Thread(runnable, "agent-harness-" + sequence.incrementAndGet());
			thread.setDaemon(true);
			return thread;
		};
	}
}

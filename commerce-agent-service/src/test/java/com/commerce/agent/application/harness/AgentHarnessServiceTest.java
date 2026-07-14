package com.commerce.agent.application.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.commerce.agent.application.service.AgentRoutingService;
import com.commerce.agent.application.service.ChatAgentService;
import com.commerce.agent.application.service.SessionService;
import com.commerce.agent.application.service.SupervisorWorkflowService;
import com.commerce.agent.application.service.UserContextService;
import com.commerce.agent.config.AgentProperties;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentHarnessServiceTest {

	private AgentProperties properties;

	private ChatAgentService chatAgentService;

	private SupervisorWorkflowService workflowService;

	private UserContextService userContextService;

	private AgentRunRegistry runRegistry;

	private HarnessToolExecutor toolExecutor;

	private AgentHarnessService harnessService;

	@BeforeEach
	void setUp() {
		properties = new AgentProperties();
		properties.getHarness().setMaxDurationMs(1000);
		properties.getHarness().setMaxConcurrentRuns(2);
		chatAgentService = mock(ChatAgentService.class);
		workflowService = mock(SupervisorWorkflowService.class);
		userContextService = new UserContextService();
		runRegistry = new AgentRunRegistry(properties);
		toolExecutor = new HarnessToolExecutor(properties);
		harnessService = new AgentHarnessService(properties, new AgentRoutingService(), chatAgentService,
				workflowService, new SessionService(properties), userContextService, runRegistry, toolExecutor);
	}

	@AfterEach
	void tearDown() {
		harnessService.shutdown();
	}

	@Test
	void recordsLifecycleAndReturnsRunId() {
		when(chatAgentService.chat(eq("你好"), anyList())).thenReturn("你好，我是 Commerce Agent");
		List<AgentRunEvent> observedEvents = new CopyOnWriteArrayList<>();
		UserContextService.UserContext user = user(7L, "CUSTOMER");

		AgentHarnessResult result = harnessService.run(new AgentRunCommand(
				"session-1",
				"你好",
				user,
				null,
				false,
				null,
				observedEvents::add
		));

		assertThat(result.success()).isTrue();
		assertThat(result.runId()).isNotBlank();
		assertThat(result.answer()).isEqualTo("你好，我是 Commerce Agent");
		AgentRunSnapshot snapshot = runRegistry.find(result.runId(), user).orElseThrow();
		assertThat(snapshot.status()).isEqualTo(AgentRunStatus.SUCCEEDED);
		assertThat(snapshot.mode()).isEqualTo("CHAT");
		assertThat(snapshot.outputChars()).isEqualTo(result.answer().length());
		assertThat(snapshot.events()).extracting(AgentRunEvent::type).containsSequence(
				AgentEventType.RUN_QUEUED,
				AgentEventType.RUN_STARTED,
				AgentEventType.SESSION_LOCKED,
				AgentEventType.ROUTE_SELECTED,
				AgentEventType.MODEL_STARTED,
				AgentEventType.MODEL_COMPLETED,
				AgentEventType.RUN_SUCCEEDED
		);
		assertThat(observedEvents).hasSameSizeAs(snapshot.events());
		verify(chatAgentService).chat(eq("你好"), anyList());
	}

	@Test
	void forcedWorkflowUsesSupervisorPath() {
		when(workflowService.analyze("给出日报")).thenReturn("# 日报\n一切正常");
		UserContextService.UserContext user = user(1L, "ADMIN");

		AgentHarnessResult result = harnessService.run(new AgentRunCommand(
				"session-2",
				"给出日报",
				user,
				AgentRoutingService.AgentMode.BUSINESS_WORKFLOW,
				false,
				null,
				null
		));

		assertThat(result.success()).isTrue();
		assertThat(runRegistry.find(result.runId(), user).orElseThrow().mode())
				.isEqualTo("BUSINESS_WORKFLOW");
		verify(workflowService).analyze("给出日报");
	}

	@Test
	void enforcesOutputBudget() {
		properties.getHarness().setMaxOutputChars(5);
		when(chatAgentService.chat(eq("长回答"), anyList())).thenReturn("123456");
		UserContextService.UserContext user = user(7L, "CUSTOMER");

		AgentHarnessResult result = harnessService
				.run(new AgentRunCommand("session-3", "长回答", user, null, false, null, null));

		assertThat(result.status()).isEqualTo(AgentRunStatus.FAILED);
		assertThat(result.error()).contains("输出字符数超过上限");
	}

	@Test
	void runIsVisibleOnlyToOwnerOrAdmin() {
		when(chatAgentService.chat(eq("私有任务"), anyList())).thenReturn("完成");
		UserContextService.UserContext owner = user(7L, "CUSTOMER");
		AgentHarnessResult result = harnessService.run(new AgentRunCommand(
				"private-session",
				"私有任务",
				owner,
				null,
				false,
				null,
				null
		));

		assertThat(runRegistry.find(result.runId(), user(8L, "CUSTOMER"))).isEmpty();
		assertThat(runRegistry.find(result.runId(), user(1L, "ADMIN"))).isPresent();
	}

	private UserContextService.UserContext user(Long userId, String roles) {
		return new UserContextService.UserContext(userId, "user-" + userId, roles, "agent:use");
	}
}

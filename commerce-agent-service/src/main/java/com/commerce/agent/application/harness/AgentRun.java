package com.commerce.agent.application.harness;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

final class AgentRun {

	private static final int MAX_STORED_QUESTION_CHARS = 2000;

	private final String runId;

	private final String sessionId;

	private final Long ownerUserId;

	private final String question;

	private final int maxEvents;

	private final int maxToolCalls;

	private final int maxOutputChars;

	private final long deadlineEpochMs;

	private final Consumer<AgentRunEvent> eventConsumer;

	private final Instant queuedAt = Instant.now();

	private final AtomicLong eventSequence = new AtomicLong();

	private final AtomicInteger toolCalls = new AtomicInteger();

	private final AtomicInteger outputChars = new AtomicInteger();

	private final AtomicReference<Future<?>> future = new AtomicReference<>();

	private final List<AgentRunEvent> events = new ArrayList<>();

	private volatile AgentRunStatus status = AgentRunStatus.QUEUED;

	private volatile Instant startedAt;

	private volatile Instant completedAt;

	private volatile String mode = "UNRESOLVED";

	private volatile String routeReason = "";

	private volatile String answer;

	private volatile String error;

	private volatile boolean cancellationRequested;

	AgentRun(String runId, String sessionId, Long ownerUserId, String question, int maxEvents, int maxToolCalls,
			int maxOutputChars, long maxDurationMs, Consumer<AgentRunEvent> eventConsumer) {
		this.runId = runId;
		this.sessionId = sessionId;
		this.ownerUserId = ownerUserId;
		this.question = truncate(question, MAX_STORED_QUESTION_CHARS);
		this.maxEvents = Math.max(10, maxEvents);
		this.maxToolCalls = Math.max(1, maxToolCalls);
		this.maxOutputChars = Math.max(1, maxOutputChars);
		this.deadlineEpochMs = System.currentTimeMillis() + Math.max(1, maxDurationMs);
		this.eventConsumer = eventConsumer;
		event(AgentEventType.RUN_QUEUED, Map.of("sessionId", sessionId));
	}

	void attach(Future<?> task) {
		if (!future.compareAndSet(null, task) && cancellationRequested) {
			task.cancel(true);
		}
		if (cancellationRequested) {
			task.cancel(true);
		}
	}

	synchronized void start() {
		checkActive();
		startedAt = Instant.now();
		status = AgentRunStatus.RUNNING;
		event(AgentEventType.RUN_STARTED, Map.of());
	}

	void sessionLocked(long waitMs) {
		event(AgentEventType.SESSION_LOCKED, Map.of("waitMs", waitMs));
	}

	void routed(String selectedMode, String reason) {
		mode = selectedMode;
		routeReason = reason;
		event(AgentEventType.ROUTE_SELECTED, Map.of("mode", selectedMode, "reason", reason));
	}

	void modelStarted() {
		checkActive();
		event(AgentEventType.MODEL_STARTED, Map.of("mode", mode));
	}

	void modelCompleted(long elapsedMs) {
		checkActive();
		event(AgentEventType.MODEL_COMPLETED, Map.of("elapsedMs", elapsedMs, "outputChars", outputChars.get()));
	}

	int beginTool(String toolName, Map<String, Object> input) {
		checkActive();
		int count = toolCalls.incrementAndGet();
		if (count > maxToolCalls) {
			throw new AgentBudgetExceededException("工具调用次数超过上限 " + maxToolCalls);
		}
		event(AgentEventType.TOOL_STARTED, Map.of("tool", toolName, "call", count, "input", safeMap(input)));
		return count;
	}

	void toolCompleted(String toolName, int call, long elapsedMs, String resultPreview) {
		checkActive();
		event(AgentEventType.TOOL_COMPLETED,
				Map.of("tool", toolName, "call", call, "elapsedMs", elapsedMs, "resultPreview", resultPreview));
	}

	void toolFailed(String toolName, int call, long elapsedMs, Throwable failure) {
		event(AgentEventType.TOOL_FAILED,
				Map.of("tool", toolName, "call", call, "elapsedMs", elapsedMs, "error", safeMessage(failure)));
	}

	void addOutputChars(int chars) {
		checkActive();
		int total = outputChars.addAndGet(Math.max(0, chars));
		if (total > maxOutputChars) {
			throw new AgentBudgetExceededException("模型输出字符数超过上限 " + maxOutputChars);
		}
	}

	synchronized void succeed(String finalAnswer) {
		checkActive();
		answer = finalAnswer;
		completedAt = Instant.now();
		status = AgentRunStatus.SUCCEEDED;
		event(AgentEventType.RUN_SUCCEEDED, Map.of("durationMs", durationMs()));
	}

	synchronized void fail(Throwable failure) {
		if (status.isTerminal()) {
			return;
		}
		error = safeMessage(failure);
		completedAt = Instant.now();
		status = AgentRunStatus.FAILED;
		event(AgentEventType.RUN_FAILED, Map.of("error", error));
	}

	synchronized boolean cancel(String reason) {
		if (status.isTerminal()) {
			return false;
		}
		cancellationRequested = true;
		error = reason;
		event(AgentEventType.RUN_CANCEL_REQUESTED, Map.of("reason", reason));
		completedAt = Instant.now();
		status = AgentRunStatus.CANCELLED;
		Future<?> task = future.get();
		if (task != null) {
			task.cancel(true);
		}
		event(AgentEventType.RUN_CANCELLED, Map.of("reason", reason));
		return true;
	}

	synchronized boolean timeOut() {
		if (status.isTerminal()) {
			return false;
		}
		cancellationRequested = true;
		error = "Agent 运行超过时间预算";
		completedAt = Instant.now();
		status = AgentRunStatus.TIMED_OUT;
		Future<?> task = future.get();
		if (task != null) {
			task.cancel(true);
		}
		event(AgentEventType.RUN_TIMED_OUT,
				Map.of("maxDurationMs", Math.max(1, deadlineEpochMs - queuedAt.toEpochMilli())));
		return true;
	}

	void checkActive() {
		if (cancellationRequested || status == AgentRunStatus.CANCELLED || Thread.currentThread().isInterrupted()) {
			throw new AgentRunAbortedException(error == null ? "Agent 运行已取消" : error);
		}
		if (System.currentTimeMillis() > deadlineEpochMs) {
			throw new AgentRunAbortedException("Agent 运行超过时间预算");
		}
	}

	void event(AgentEventType type, Map<String, Object> data) {
		AgentRunEvent event = new AgentRunEvent(runId, eventSequence.incrementAndGet(), Instant.now(), type,
				Map.copyOf(safeMap(data)));
		synchronized (events) {
			events.add(event);
			while (events.size() > maxEvents) {
				events.remove(0);
			}
		}
		try {
			eventConsumer.accept(event);
		} catch (RuntimeException ignored) {
			// Observability consumers must not be able to fail the agent run.
		}
	}

	AgentHarnessResult result() {
		return new AgentHarnessResult(runId, sessionId, status, answer, error);
	}

	AgentRunSnapshot snapshot() {
		List<AgentRunEvent> eventCopy;
		synchronized (events) {
			eventCopy = List.copyOf(events);
		}
		return new AgentRunSnapshot(runId, sessionId, status, mode, routeReason, question, answer, error,
				toolCalls.get(), outputChars.get(), queuedAt, startedAt, completedAt, durationMs(), eventCopy);
	}

	String runId() {
		return runId;
	}

	String sessionId() {
		return sessionId;
	}

	Long ownerUserId() {
		return ownerUserId;
	}

	Instant queuedAt() {
		return queuedAt;
	}

	private long durationMs() {
		Instant end = completedAt == null ? Instant.now() : completedAt;
		Instant start = startedAt == null ? queuedAt : startedAt;
		return Math.max(0, Duration.between(start, end).toMillis());
	}

	private Map<String, Object> safeMap(Map<String, Object> source) {
		Map<String, Object> safe = new LinkedHashMap<>();
		if (source != null) {
			source.forEach((key, value) -> safe.put(key, value == null ? "" : value));
		}
		return safe;
	}

	private String safeMessage(Throwable failure) {
		if (failure == null) {
			return "Unknown agent error";
		}
		String message = failure.getMessage();
		return message == null || message.isBlank() ? failure.getClass().getSimpleName() : message;
	}

	private String truncate(String value, int maxChars) {
		if (value == null || value.length() <= maxChars) {
			return value;
		}
		return value.substring(0, maxChars) + "...";
	}
}

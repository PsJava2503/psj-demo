package com.commerce.agent.application.harness;

import java.time.Instant;
import java.util.List;

public record AgentRunSnapshot(String runId, String sessionId, AgentRunStatus status, String mode, String routeReason,
		String question, String answer, String error, int toolCalls, int outputChars, Instant queuedAt,
		Instant startedAt, Instant completedAt, long durationMs, List<AgentRunEvent> events) {
}

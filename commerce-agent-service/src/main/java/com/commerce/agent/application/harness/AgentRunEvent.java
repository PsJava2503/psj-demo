package com.commerce.agent.application.harness;

import java.time.Instant;
import java.util.Map;

public record AgentRunEvent(String runId, long sequence, Instant timestamp, AgentEventType type,
		Map<String, Object> data) {
}

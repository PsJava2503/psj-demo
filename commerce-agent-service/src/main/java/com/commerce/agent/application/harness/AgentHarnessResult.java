package com.commerce.agent.application.harness;

public record AgentHarnessResult(String runId, String sessionId, AgentRunStatus status, String answer, String error) {

	public boolean success() {
		return status == AgentRunStatus.SUCCEEDED;
	}
}

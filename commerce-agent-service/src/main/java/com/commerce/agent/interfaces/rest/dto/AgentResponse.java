package com.commerce.agent.interfaces.rest.dto;

public record AgentResponse(boolean success, String sessionId, String runId, String answer, String error) {

	public static AgentResponse success(String sessionId, String runId, String answer) {
		return new AgentResponse(true, sessionId, runId, answer, null);
	}

	public static AgentResponse error(String sessionId, String runId, String error) {
		return new AgentResponse(false, sessionId, runId, null, error);
	}
}

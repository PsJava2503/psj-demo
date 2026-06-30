package com.commerce.agent.interfaces.rest.dto;

public record AgentResponse(
		boolean success,
		String sessionId,
		String answer,
		String error
) {

	public static AgentResponse success(String sessionId, String answer) {
		return new AgentResponse(true, sessionId, answer, null);
	}

	public static AgentResponse error(String sessionId, String error) {
		return new AgentResponse(false, sessionId, null, error);
	}
}

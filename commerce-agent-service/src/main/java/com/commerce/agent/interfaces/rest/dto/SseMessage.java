package com.commerce.agent.interfaces.rest.dto;

import com.commerce.agent.application.harness.AgentRunEvent;

public record SseMessage(String type, Object data) {

	public static SseMessage content(String data) {
		return new SseMessage("content", data);
	}

	public static SseMessage error(String data) {
		return new SseMessage("error", data);
	}

	public static SseMessage status(AgentRunEvent event) {
		return new SseMessage("status", event);
	}

	public static SseMessage done() {
		return new SseMessage("done", "");
	}
}

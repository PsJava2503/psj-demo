package com.commerce.agent.interfaces.rest.dto;

public record SseMessage(
		String type,
		String data
) {

	public static SseMessage content(String data) {
		return new SseMessage("content", data);
	}

	public static SseMessage error(String data) {
		return new SseMessage("error", data);
	}

	public static SseMessage done() {
		return new SseMessage("done", "");
	}
}

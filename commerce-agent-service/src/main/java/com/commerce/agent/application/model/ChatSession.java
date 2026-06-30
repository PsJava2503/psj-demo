package com.commerce.agent.application.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatSession {

	private final String id;

	private final int maxPairs;

	private final List<ChatMessage> history = new ArrayList<>();

	private Instant updatedAt = Instant.now();

	public ChatSession(String id, int maxPairs) {
		this.id = id;
		this.maxPairs = maxPairs;
	}

	public synchronized void addExchange(String question, String answer) {
		history.add(ChatMessage.user(question));
		history.add(ChatMessage.assistant(answer));
		trimHistory();
		updatedAt = Instant.now();
	}

	public synchronized List<ChatMessage> history() {
		return List.copyOf(history);
	}

	public synchronized void clear() {
		history.clear();
		updatedAt = Instant.now();
	}

	public String id() {
		return id;
	}

	public synchronized int messagePairCount() {
		return history.size() / 2;
	}

	public Instant updatedAt() {
		return updatedAt;
	}

	private void trimHistory() {
		int maxMessages = Math.max(1, maxPairs) * 2;
		while (history.size() > maxMessages) {
			history.remove(0);
		}
	}
}

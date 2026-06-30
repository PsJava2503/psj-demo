package com.commerce.agent.application.model;

public record SearchResult(
		String source,
		int chunkIndex,
		String title,
		String content,
		float score
) {
}

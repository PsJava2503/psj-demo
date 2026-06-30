package com.commerce.agent.application.model;

import java.util.List;
import java.util.Map;

public record KnowledgeChunk(
		String id,
		String source,
		int chunkIndex,
		String title,
		String content,
		List<Float> vector,
		Map<String, Object> metadata
) {
}

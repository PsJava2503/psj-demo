package com.commerce.agent.application.service;

import com.commerce.agent.application.model.KnowledgeChunk;
import com.commerce.agent.config.AgentProperties;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DocumentChunkService {

	private final AgentProperties properties;

	public DocumentChunkService(AgentProperties properties) {
		this.properties = properties;
	}

	public List<KnowledgeChunk> chunk(String source, String content) {
		int maxSize = Math.max(100, properties.getRag().getChunk().getMaxSize());
		int overlap = Math.max(0, Math.min(properties.getRag().getChunk().getOverlap(), maxSize / 2));
		List<KnowledgeChunk> chunks = new ArrayList<>();
		int start = 0;
		int index = 0;
		String title = extractTitle(content);
		while (start < content.length()) {
			int end = Math.min(content.length(), start + maxSize);
			String chunkText = content.substring(start, end).trim();
			if (StringUtils.hasText(chunkText)) {
				String id = UUID.nameUUIDFromBytes((source + ":" + index).getBytes(StandardCharsets.UTF_8)).toString();
				chunks.add(new KnowledgeChunk(
						id,
						source,
						index,
						title,
						chunkText,
						List.of(),
						Map.of("source", source, "chunkIndex", index, "title", title)
				));
			}
			if (end == content.length()) {
				break;
			}
			start = Math.max(end - overlap, start + 1);
			index++;
		}
		return chunks;
	}

	private String extractTitle(String content) {
		for (String line : content.split("\\R")) {
			String trimmed = line.trim();
			if (trimmed.startsWith("#")) {
				return trimmed.replaceFirst("^#+", "").trim();
			}
			if (StringUtils.hasText(trimmed)) {
				return trimmed.length() > 80 ? trimmed.substring(0, 80) : trimmed;
			}
		}
		return "";
	}
}

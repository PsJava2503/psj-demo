package com.commerce.agent.application.service;

import com.commerce.agent.application.model.SearchResult;
import com.commerce.agent.config.AgentProperties;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class KnowledgeSearchService {

	private final AgentProperties properties;

	private final EmbeddingService embeddingService;

	private final KnowledgeRepository knowledgeRepository;

	public KnowledgeSearchService(
			AgentProperties properties,
			EmbeddingService embeddingService,
			KnowledgeRepository knowledgeRepository
	) {
		this.properties = properties;
		this.embeddingService = embeddingService;
		this.knowledgeRepository = knowledgeRepository;
	}

	public List<SearchResult> search(String query) {
		if (!properties.getRag().isEnabled() || !StringUtils.hasText(query)) {
			return List.of();
		}
		return knowledgeRepository.search(embeddingService.embed(query), properties.getRag().getTopK());
	}

	public String searchAsText(String query) {
		List<SearchResult> results = search(query);
		if (results.isEmpty()) {
			return "没有检索到相关内部知识。";
		}
		StringBuilder builder = new StringBuilder("内部知识库检索结果：\n");
		for (SearchResult result : results) {
			builder.append("- 来源: ").append(result.source())
					.append(", 分片: ").append(result.chunkIndex())
					.append(", 相似度: ").append(String.format("%.3f", result.score()))
					.append("\n")
					.append(result.content())
					.append("\n");
		}
		return builder.toString();
	}
}

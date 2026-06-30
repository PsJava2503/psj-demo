package com.commerce.agent.infrastructure.vector;

import com.commerce.agent.application.model.KnowledgeChunk;
import com.commerce.agent.application.model.SearchResult;
import com.commerce.agent.application.service.EmbeddingService;
import com.commerce.agent.application.service.KnowledgeRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "agent.rag.use-milvus", havingValue = "false", matchIfMissing = true)
public class InMemoryKnowledgeRepository implements KnowledgeRepository {

	private final EmbeddingService embeddingService;

	private final Map<String, List<KnowledgeChunk>> chunksBySource = new ConcurrentHashMap<>();

	public InMemoryKnowledgeRepository(EmbeddingService embeddingService) {
		this.embeddingService = embeddingService;
	}

	@Override
	public void replaceSource(String source, List<KnowledgeChunk> chunks) {
		chunksBySource.put(source, List.copyOf(chunks));
	}

	@Override
	public List<SearchResult> search(List<Float> queryVector, int topK) {
		return chunksBySource.values().stream()
				.flatMap(List::stream)
				.map(chunk -> new SearchResult(
						chunk.source(),
						chunk.chunkIndex(),
						chunk.title(),
						chunk.content(),
						embeddingService.cosine(queryVector, chunk.vector())
				))
				.sorted(Comparator.comparing(SearchResult::score).reversed())
				.limit(Math.max(1, topK))
				.toList();
	}
}

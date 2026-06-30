package com.commerce.agent.application.service;

import com.commerce.agent.application.model.KnowledgeChunk;
import com.commerce.agent.application.model.SearchResult;
import java.util.List;

public interface KnowledgeRepository {

	void replaceSource(String source, List<KnowledgeChunk> chunks);

	List<SearchResult> search(List<Float> queryVector, int topK);
}

package com.commerce.agent.infrastructure.vector;

import com.commerce.agent.application.model.KnowledgeChunk;
import com.commerce.agent.application.model.SearchResult;
import com.commerce.agent.application.service.KnowledgeRepository;
import com.commerce.agent.config.MilvusProperties;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "agent.rag.use-milvus", havingValue = "true")
public class MilvusKnowledgeRepository implements KnowledgeRepository {

	private final MilvusServiceClient client;

	private final MilvusProperties properties;

	public MilvusKnowledgeRepository(MilvusServiceClient client, MilvusProperties properties) {
		this.client = client;
		this.properties = properties;
	}

	@Override
	public void replaceSource(String source, List<KnowledgeChunk> chunks) {
		deleteSource(source);
		if (chunks.isEmpty()) {
			return;
		}
		List<String> ids = new ArrayList<>();
		List<String> sources = new ArrayList<>();
		List<Long> indexes = new ArrayList<>();
		List<String> titles = new ArrayList<>();
		List<String> contents = new ArrayList<>();
		List<List<Float>> vectors = new ArrayList<>();
		for (KnowledgeChunk chunk : chunks) {
			ids.add(chunk.id());
			sources.add(chunk.source());
			indexes.add((long) chunk.chunkIndex());
			titles.add(truncate(chunk.title(), 512));
			contents.add(truncate(chunk.content(), 8192));
			vectors.add(chunk.vector());
		}
		List<InsertParam.Field> fields = List.of(
				new InsertParam.Field("id", ids),
				new InsertParam.Field("source", sources),
				new InsertParam.Field("chunkIndex", indexes),
				new InsertParam.Field("title", titles),
				new InsertParam.Field("content", contents),
				new InsertParam.Field("vector", vectors)
		);
		R<MutationResult> inserted = client.insert(InsertParam.newBuilder()
				.withCollectionName(properties.getCollection())
				.withFields(fields)
				.build());
		if (inserted.getStatus() != 0) {
			throw new IllegalStateException("Failed to insert Milvus chunks: " + inserted.getMessage());
		}
	}

	@Override
	public List<SearchResult> search(List<Float> queryVector, int topK) {
		loadCollection();
		R<SearchResults> searched = client.search(SearchParam.newBuilder()
				.withCollectionName(properties.getCollection())
				.withVectorFieldName("vector")
				.withVectors(Collections.singletonList(queryVector))
				.withTopK(Math.max(1, topK))
				.withMetricType(MetricType.L2)
				.withOutFields(List.of("source", "chunkIndex", "title", "content"))
				.withParams("{\"nprobe\":10}")
				.build());
		if (searched.getStatus() != 0) {
			throw new IllegalStateException("Failed to search Milvus chunks: " + searched.getMessage());
		}
		SearchResultsWrapper wrapper = new SearchResultsWrapper(searched.getData().getResults());
		List<SearchResult> results = new ArrayList<>();
		for (int i = 0; i < wrapper.getRowRecords(0).size(); i++) {
			results.add(new SearchResult(
					String.valueOf(wrapper.getFieldData("source", 0).get(i)),
					((Number) wrapper.getFieldData("chunkIndex", 0).get(i)).intValue(),
					String.valueOf(wrapper.getFieldData("title", 0).get(i)),
					String.valueOf(wrapper.getFieldData("content", 0).get(i)),
					wrapper.getIDScore(0).get(i).getScore()
			));
		}
		return results;
	}

	private void deleteSource(String source) {
		loadCollection();
		String escapedSource = source.replace("\\", "\\\\").replace("\"", "\\\"");
		R<MutationResult> deleted = client.delete(DeleteParam.newBuilder()
				.withCollectionName(properties.getCollection())
				.withExpr("source == \"" + escapedSource + "\"")
				.build());
		if (deleted.getStatus() != 0) {
			throw new IllegalStateException("Failed to delete old Milvus chunks: " + deleted.getMessage());
		}
	}

	private void loadCollection() {
		R<RpcStatus> loaded = client.loadCollection(LoadCollectionParam.newBuilder()
				.withCollectionName(properties.getCollection())
				.build());
		if (loaded.getStatus() != 0 && loaded.getStatus() != 65535) {
			throw new IllegalStateException("Failed to load Milvus collection: " + loaded.getMessage());
		}
	}

	private String truncate(String value, int maxLength) {
		if (value == null) {
			return "";
		}
		return value.length() <= maxLength ? value : value.substring(0, maxLength);
	}
}

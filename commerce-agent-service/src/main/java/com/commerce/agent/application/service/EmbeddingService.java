package com.commerce.agent.application.service;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.alibaba.dashscope.utils.Constants;
import com.commerce.agent.config.AgentProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmbeddingService {

	private static final int MOCK_DIMENSION = 128;

	private final AgentProperties properties;

	private final String apiKey;

	private final TextEmbedding textEmbedding = new TextEmbedding();

	public EmbeddingService(AgentProperties properties, @Value("${spring.ai.dashscope.api-key:}") String apiKey) {
		this.properties = properties;
		this.apiKey = apiKey;
	}

	public List<Float> embed(String content) {
		if (properties.isMockEnabled() || !StringUtils.hasText(apiKey) || "mock-api-key".equals(apiKey)) {
			return mockEmbedding(content);
		}
		try {
			Constants.apiKey = apiKey;
			TextEmbeddingParam param = TextEmbeddingParam.builder()
					.model(properties.getRag().getEmbeddingModel())
					.texts(Collections.singletonList(content))
					.build();
			TextEmbeddingResult result = textEmbedding.call(param);
			if (result == null || result.getOutput() == null || result.getOutput().getEmbeddings().isEmpty()) {
				throw new IllegalStateException("DashScope embedding returned empty result");
			}
			TextEmbeddingResultItem item = result.getOutput().getEmbeddings().get(0);
			List<Float> vector = new ArrayList<>(item.getEmbedding().size());
			for (Double value : item.getEmbedding()) {
				vector.add(value.floatValue());
			}
			return vector;
		}
		catch (Exception error) {
			throw new IllegalStateException("Failed to generate DashScope embedding: " + error.getMessage(), error);
		}
	}

	public float cosine(List<Float> left, List<Float> right) {
		int size = Math.min(left.size(), right.size());
		if (size == 0) {
			return 0;
		}
		float dot = 0;
		float leftNorm = 0;
		float rightNorm = 0;
		for (int i = 0; i < size; i++) {
			float l = left.get(i);
			float r = right.get(i);
			dot += l * r;
			leftNorm += l * l;
			rightNorm += r * r;
		}
		if (leftNorm == 0 || rightNorm == 0) {
			return 0;
		}
		return (float) (dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm)));
	}

	private List<Float> mockEmbedding(String content) {
		byte[] digest = digest(content == null ? "" : content);
		List<Float> vector = new ArrayList<>(MOCK_DIMENSION);
		for (int i = 0; i < MOCK_DIMENSION; i++) {
			int value = digest[i % digest.length] & 0xff;
			vector.add((value - 128) / 128.0f);
		}
		return vector;
	}

	private byte[] digest(String content) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8));
		}
		catch (NoSuchAlgorithmException error) {
			throw new IllegalStateException(error);
		}
	}
}

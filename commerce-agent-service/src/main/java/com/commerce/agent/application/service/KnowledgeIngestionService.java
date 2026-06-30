package com.commerce.agent.application.service;

import com.commerce.agent.application.model.KnowledgeChunk;
import com.commerce.agent.config.AgentProperties;
import com.commerce.agent.interfaces.rest.dto.KnowledgeUploadResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class KnowledgeIngestionService {

	private final AgentProperties properties;

	private final DocumentChunkService chunkService;

	private final EmbeddingService embeddingService;

	private final KnowledgeRepository knowledgeRepository;

	public KnowledgeIngestionService(
			AgentProperties properties,
			DocumentChunkService chunkService,
			EmbeddingService embeddingService,
			KnowledgeRepository knowledgeRepository
	) {
		this.properties = properties;
		this.chunkService = chunkService;
		this.embeddingService = embeddingService;
		this.knowledgeRepository = knowledgeRepository;
	}

	public KnowledgeUploadResponse upload(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("Uploaded file is empty");
		}
		String fileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "knowledge.txt" : file.getOriginalFilename());
		validateExtension(fileName);
		Path uploadDir = Path.of(properties.getRag().getUploadPath()).normalize();
		Files.createDirectories(uploadDir);
		Path target = uploadDir.resolve(fileName).normalize();
		if (!target.startsWith(uploadDir)) {
			throw new IllegalArgumentException("Invalid upload path");
		}
		file.transferTo(target);
		String content = Files.readString(target);
		List<KnowledgeChunk> chunks = chunkService.chunk(target.toString(), content).stream()
				.map(chunk -> new KnowledgeChunk(
						chunk.id(),
						chunk.source(),
						chunk.chunkIndex(),
						chunk.title(),
						chunk.content(),
						embeddingService.embed(chunk.content()),
						chunk.metadata()
				))
				.toList();
		knowledgeRepository.replaceSource(target.toString(), chunks);
		return new KnowledgeUploadResponse(fileName, chunks.size(), true, "Knowledge indexed successfully");
	}

	private void validateExtension(String fileName) {
		int dot = fileName.lastIndexOf('.');
		String extension = dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase();
		if (!properties.getRag().getAllowedExtensions().contains(extension)) {
			throw new IllegalArgumentException("Unsupported file extension: " + extension);
		}
	}
}

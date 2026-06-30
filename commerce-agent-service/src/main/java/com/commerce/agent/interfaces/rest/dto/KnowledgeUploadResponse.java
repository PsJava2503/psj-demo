package com.commerce.agent.interfaces.rest.dto;

public record KnowledgeUploadResponse(
		String fileName,
		int chunks,
		boolean indexed,
		String message
) {
}

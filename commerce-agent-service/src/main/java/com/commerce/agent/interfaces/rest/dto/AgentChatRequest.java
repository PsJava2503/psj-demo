package com.commerce.agent.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record AgentChatRequest(
		@JsonAlias("Id")
		String sessionId,
		@JsonAlias("Question")
		@NotBlank
		String question
) {
}

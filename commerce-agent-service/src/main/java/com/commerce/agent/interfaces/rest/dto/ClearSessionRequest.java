package com.commerce.agent.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record ClearSessionRequest(
		@JsonAlias("Id")
		@NotBlank
		String sessionId
) {
}

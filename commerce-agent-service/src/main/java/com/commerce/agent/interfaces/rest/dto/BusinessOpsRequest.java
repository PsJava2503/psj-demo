package com.commerce.agent.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record BusinessOpsRequest(
		@JsonAlias("Id")
		String sessionId,
		@JsonAlias({"Task", "Question"})
		String task
) {
}

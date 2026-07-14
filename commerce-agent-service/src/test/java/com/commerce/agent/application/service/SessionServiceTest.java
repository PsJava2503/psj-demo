package com.commerce.agent.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.commerce.agent.application.model.ChatSession;
import com.commerce.agent.config.AgentProperties;
import org.junit.jupiter.api.Test;

class SessionServiceTest {

	@Test
	void isolatesClientSessionIdsByOwner() {
		SessionService service = new SessionService(new AgentProperties());
		ChatSession firstUser = service.getOrCreate("shared-client-id", 1L);
		ChatSession secondUser = service.getOrCreate("shared-client-id", 2L);
		firstUser.addExchange("private question", "private answer");

		assertThat(firstUser.id()).isEqualTo("shared-client-id");
		assertThat(secondUser.id()).isEqualTo("shared-client-id");
		assertThat(secondUser.history()).isEmpty();
		assertThat(service.clear("shared-client-id", 2L)).isTrue();
		assertThat(firstUser.history()).hasSize(2);
	}
}

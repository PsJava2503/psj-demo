package com.commerce.agent.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AgentRoutingServiceTest {

	private final AgentRoutingService routingService = new AgentRoutingService();

	@Test
	void routesBusinessDiagnosisToWorkflow() {
		assertThat(routingService.route("帮我排查订单支付异常").mode())
				.isEqualTo(AgentRoutingService.AgentMode.BUSINESS_WORKFLOW);
	}

	@Test
	void routesSimpleQuestionToChat() {
		assertThat(routingService.route("现在几点").mode()).isEqualTo(AgentRoutingService.AgentMode.CHAT);
	}
}

package com.commerce.agent.agent.tool;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.commerce.agent.application.harness.HarnessToolExecutor;
import com.commerce.agent.application.service.UserContextService;
import com.commerce.agent.config.AgentProperties;
import com.commerce.agent.infrastructure.rpc.CartClient;
import com.commerce.agent.infrastructure.rpc.OrderClient;
import com.commerce.agent.infrastructure.rpc.ProductClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class CommerceToolsTest {

	private final UserContextService userContextService = new UserContextService();

	@AfterEach
	void clearContext() {
		userContextService.clear();
	}

	@Test
	void customerCannotQueryAnotherUsersOrders() {
		OrderClient orderClient = mock(OrderClient.class);
		CommerceTools tools = new CommerceTools(mock(ProductClient.class), orderClient, mock(CartClient.class),
				userContextService, new ObjectMapper(), new HarnessToolExecutor(new AgentProperties()));
		userContextService.set(new UserContextService.UserContext(7L, "customer", "CUSTOMER", "agent:use"));

		assertThatThrownBy(() -> tools.queryOrders(8L, null)).isInstanceOf(SecurityException.class)
				.hasMessageContaining("无权查询其他用户");
		verifyNoInteractions(orderClient);
	}
}

package com.commerce.agent.agent.tool;

import com.commerce.agent.application.service.UserContextService;
import com.commerce.agent.infrastructure.rpc.CartClient;
import com.commerce.agent.infrastructure.rpc.OrderClient;
import com.commerce.agent.infrastructure.rpc.ProductClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CommerceTools {

	private final ProductClient productClient;

	private final OrderClient orderClient;

	private final CartClient cartClient;

	private final UserContextService userContextService;

	private final ObjectMapper objectMapper;

	public CommerceTools(
			ProductClient productClient,
			OrderClient orderClient,
			CartClient cartClient,
			UserContextService userContextService,
			ObjectMapper objectMapper
	) {
		this.productClient = productClient;
		this.orderClient = orderClient;
		this.cartClient = cartClient;
		this.userContextService = userContextService;
		this.objectMapper = objectMapper;
	}

	@Tool(description = "查询商品列表，可按商品名称模糊查询；只返回未删除且启用的商品")
	public String queryProducts(@ToolParam(description = "商品名称，可为空") String name) {
		return toJson(productClient.query(null, blankToNull(name), null, true, false));
	}

	@Tool(description = "根据商品 ID 查询商品详情")
	public String getProduct(@ToolParam(description = "商品 ID") Long productId) {
		return toJson(productClient.get(productId));
	}

	@Tool(description = "查询当前用户或指定用户的订单。默认使用网关透传的当前用户 ID")
	public String queryOrders(
			@ToolParam(description = "用户 ID，可为空；为空时使用当前登录用户") Long userId,
			@ToolParam(description = "订单状态，可为空") String status
	) {
		Long effectiveUserId = userId == null ? userContextService.current().userId() : userId;
		return toJson(orderClient.query(null, effectiveUserId, blankToNull(status)));
	}

	@Tool(description = "根据订单 ID 查询订单详情")
	public String getOrder(@ToolParam(description = "订单 ID") Long orderId) {
		return toJson(orderClient.get(orderId));
	}

	@Tool(description = "查询当前用户或指定用户的购物车，只返回未删除条目")
	public String queryCart(@ToolParam(description = "用户 ID，可为空；为空时使用当前登录用户") Long userId) {
		Long effectiveUserId = userId == null ? userContextService.current().userId() : userId;
		return toJson(cartClient.query(null, effectiveUserId, null, null, false));
	}

	@Tool(description = "读取网关透传的当前登录用户上下文")
	public String currentUserContext() {
		UserContextService.UserContext context = userContextService.current();
		return toJson(Map.of(
				"userId", context.userId() == null ? "" : context.userId(),
				"username", context.username() == null ? "" : context.username(),
				"roles", context.roles() == null ? "" : context.roles(),
				"permissions", context.permissions() == null ? "" : context.permissions()
		));
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}

	private String toJson(Object value) {
		try {
			if (value instanceof List<?> list && list.isEmpty()) {
				return "[]";
			}
			return objectMapper.writeValueAsString(value);
		}
		catch (JsonProcessingException error) {
			throw new IllegalStateException("Failed to serialize tool result", error);
		}
	}
}

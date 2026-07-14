package com.commerce.agent.agent.tool;

import com.commerce.agent.application.harness.HarnessToolExecutor;
import com.commerce.agent.application.service.UserContextService;
import com.commerce.agent.infrastructure.rpc.CartClient;
import com.commerce.agent.infrastructure.rpc.OrderClient;
import com.commerce.agent.infrastructure.rpc.ProductClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.commerce.security.CommerceRoles;
import java.util.LinkedHashMap;
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

	private final HarnessToolExecutor toolExecutor;

	public CommerceTools(ProductClient productClient, OrderClient orderClient, CartClient cartClient,
			UserContextService userContextService, ObjectMapper objectMapper, HarnessToolExecutor toolExecutor) {
		this.productClient = productClient;
		this.orderClient = orderClient;
		this.cartClient = cartClient;
		this.userContextService = userContextService;
		this.objectMapper = objectMapper;
		this.toolExecutor = toolExecutor;
	}

	@Tool(description = "查询商品列表，可按商品名称模糊查询；只返回未删除且启用的商品")
	public String queryProducts(@ToolParam(description = "商品名称，可为空") String name) {
		return toolExecutor.execute("queryProducts", input("name", name),
				() -> toJson(productClient.query(null, blankToNull(name), null, true, false)));
	}

	@Tool(description = "根据商品 ID 查询商品详情")
	public String getProduct(@ToolParam(description = "商品 ID") Long productId) {
		return toolExecutor.execute("getProduct", input("productId", productId),
				() -> toJson(productClient.get(productId)));
	}

	@Tool(description = "查询当前用户或指定用户的订单。默认使用网关透传的当前用户 ID")
	public String queryOrders(@ToolParam(description = "用户 ID，可为空；为空时使用当前登录用户") Long userId,
			@ToolParam(description = "订单状态，可为空") String status) {
		return toolExecutor.execute("queryOrders", input("userId", userId, "status", status), () -> {
			Long effectiveUserId = accessibleUserId(userId);
			return toJson(orderClient.query(null, effectiveUserId, blankToNull(status)));
		});
	}

	@Tool(description = "根据订单 ID 查询订单详情")
	public String getOrder(@ToolParam(description = "订单 ID") Long orderId) {
		return toolExecutor.execute("getOrder", input("orderId", orderId), () -> {
			Map<String, Object> order = orderClient.get(orderId);
			ensureOrderAccessible(order);
			return toJson(order);
		});
	}

	@Tool(description = "查询当前用户或指定用户的购物车，只返回未删除条目")
	public String queryCart(@ToolParam(description = "用户 ID，可为空；为空时使用当前登录用户") Long userId) {
		return toolExecutor.execute("queryCart", input("userId", userId), () -> {
			Long effectiveUserId = accessibleUserId(userId);
			return toJson(cartClient.query(null, effectiveUserId, null, null, false));
		});
	}

	@Tool(description = "读取网关透传的当前登录用户上下文")
	public String currentUserContext() {
		return toolExecutor.execute("currentUserContext", Map.of(), () -> {
			UserContextService.UserContext context = userContextService.current();
			return toJson(Map.of("userId", context.userId() == null ? "" : context.userId(), "username",
					context.username() == null ? "" : context.username(), "roles",
					context.roles() == null ? "" : context.roles(), "permissions",
					context.permissions() == null ? "" : context.permissions()));
		});
	}

	private Long accessibleUserId(Long requestedUserId) {
		UserContextService.UserContext context = userContextService.current();
		if (context.userId() == null) {
			throw new SecurityException("缺少登录用户上下文，不能查询用户业务数据");
		}
		Long effectiveUserId = requestedUserId == null ? context.userId() : requestedUserId;
		if (!effectiveUserId.equals(context.userId()) && !context.hasRole(CommerceRoles.ADMIN)) {
			throw new SecurityException("无权查询其他用户的业务数据");
		}
		return effectiveUserId;
	}

	private void ensureOrderAccessible(Map<String, Object> order) {
		if (order == null || order.isEmpty()) {
			return;
		}
		UserContextService.UserContext context = userContextService.current();
		if (context.hasRole(CommerceRoles.ADMIN)) {
			return;
		}
		Long orderUserId = toLong(order.get("userId"));
		if (context.userId() == null || orderUserId == null || !context.userId().equals(orderUserId)) {
			throw new SecurityException("无权查询该订单");
		}
	}

	private Long toLong(Object value) {
		if (value instanceof Number number) {
			return number.longValue();
		}
		try {
			return value == null ? null : Long.valueOf(String.valueOf(value));
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	private Map<String, Object> input(Object... pairs) {
		Map<String, Object> input = new LinkedHashMap<>();
		for (int index = 0; index + 1 < pairs.length; index += 2) {
			Object value = pairs[index + 1];
			input.put(String.valueOf(pairs[index]), value == null ? "" : value);
		}
		return input;
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
		} catch (JsonProcessingException error) {
			throw new IllegalStateException("Failed to serialize tool result", error);
		}
	}
}

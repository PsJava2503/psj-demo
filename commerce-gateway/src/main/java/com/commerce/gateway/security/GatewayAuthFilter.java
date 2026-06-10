package com.commerce.gateway.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.commerce.security.CommercePermissions;
import com.commerce.security.SecurityHeaders;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayAuthFilter implements GlobalFilter, Ordered {

	private static final String TOKEN_HEADER = "satoken";

	private static final Map<String, String> PERMISSIONS_BY_PATH_PREFIX = Map.of(
			"/api/orders", CommercePermissions.ORDER_CREATE,
			"/api/payments", CommercePermissions.PAYMENT_PAY,
			"/api/inventory", CommercePermissions.INVENTORY_VIEW,
			"/api/products", CommercePermissions.PRODUCT_VIEW,
			"/api/users", CommercePermissions.USER_VIEW,
			"/api/notifications", CommercePermissions.NOTIFICATION_SEND,
			"/api/addresses", CommercePermissions.ADDRESS_VIEW
	);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getPath().value();
		if (!path.startsWith("/api/") || path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
			return chain.filter(exchange);
		}

		String token = exchange.getRequest().getHeaders().getFirst(TOKEN_HEADER);
		if (token == null || token.isBlank()) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		try {
			Object loginId = StpUtil.getLoginIdByToken(token);
			SaSession tokenSession = StpUtil.getTokenSessionByToken(token);
			Long userId = toLong(tokenSession.get("userId"), loginId);
			String username = toStringValue(tokenSession.get("username"));
			List<String> roles = asStringList(tokenSession.get("roles"));
			List<String> permissions = asStringList(tokenSession.get("permissions"));
			return authorizeAndForward(exchange, chain, path, userId, username, roles, permissions);
		}
		catch (Exception error) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}
	}

	private Mono<Void> authorizeAndForward(
			ServerWebExchange exchange,
			GatewayFilterChain chain,
			String path,
			Long userId,
			String username,
			List<String> roles,
			List<String> permissions
	) {
		String requiredPermission = requiredPermission(path);
		if (requiredPermission != null && !permissions.contains(requiredPermission)) {
			exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
			return exchange.getResponse().setComplete();
		}

		ServerHttpRequest request = exchange.getRequest().mutate()
				.header(SecurityHeaders.USER_ID, String.valueOf(userId))
				.header(SecurityHeaders.USERNAME, username)
				.header(SecurityHeaders.ROLES, String.join(",", roles))
				.header(SecurityHeaders.PERMISSIONS, String.join(",", permissions))
				.header(HttpHeaders.AUTHORIZATION, "")
				.build();
		return chain.filter(exchange.mutate().request(request).build());
	}

	private String requiredPermission(String path) {
		return PERMISSIONS_BY_PATH_PREFIX.entrySet().stream()
				.filter(entry -> path.startsWith(entry.getKey()))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElse(null);
	}

	private List<String> asStringList(Object value) {
		if (value instanceof List<?> list) {
			return list.stream().map(String::valueOf).toList();
		}
		return List.of();
	}

	private String toStringValue(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	private Long toLong(Object value, Object fallback) {
		Object target = value == null ? fallback : value;
		return Long.valueOf(String.valueOf(target));
	}

	@Override
	public int getOrder() {
		return -100;
	}

}

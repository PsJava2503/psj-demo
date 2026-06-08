package com.psj.commerce.gateway.security;

import com.psj.commerce.security.AuthSession;
import com.psj.commerce.security.CommercePermissions;
import com.psj.commerce.security.SecurityHeaders;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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
			"/api/notifications", CommercePermissions.NOTIFICATION_SEND
	);

	private final WebClient webClient;

	public GatewayAuthFilter(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://psj-commerce-user-service").build();
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getPath().value();
		if (!path.startsWith("/api/") || path.equals("/api/auth/login")) {
			return chain.filter(exchange);
		}

		String token = exchange.getRequest().getHeaders().getFirst(TOKEN_HEADER);
		if (token == null || token.isBlank()) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		return webClient.get()
				.uri("/api/auth/session")
				.header(TOKEN_HEADER, token)
				.retrieve()
				.bodyToMono(AuthSession.class)
				.flatMap(session -> authorizeAndForward(exchange, chain, path, session))
				.onErrorResume(error -> {
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
					return exchange.getResponse().setComplete();
				});
	}

	private Mono<Void> authorizeAndForward(
			ServerWebExchange exchange,
			GatewayFilterChain chain,
			String path,
			AuthSession session
	) {
		String requiredPermission = requiredPermission(path);
		if (requiredPermission != null && !session.permissions().contains(requiredPermission)) {
			exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
			return exchange.getResponse().setComplete();
		}

		ServerHttpRequest request = exchange.getRequest().mutate()
				.header(SecurityHeaders.USER_ID, session.userId().toString())
				.header(SecurityHeaders.USERNAME, session.username())
				.header(SecurityHeaders.ROLES, String.join(",", session.roles()))
				.header(SecurityHeaders.PERMISSIONS, String.join(",", session.permissions()))
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

	@Override
	public int getOrder() {
		return -100;
	}

}

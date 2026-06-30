package com.commerce.agent.infrastructure.rpc;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commerce-cart-service")
public interface CartClient {

	@GetMapping("/api/carts")
	List<Map<String, Object>> query(
			@RequestParam(name = "id", required = false) Long id,
			@RequestParam(name = "userId", required = false) Long userId,
			@RequestParam(name = "productId", required = false) Long productId,
			@RequestParam(name = "selected", required = false) Boolean selected,
			@RequestParam(name = "deleted", required = false) Boolean deleted
	);
}

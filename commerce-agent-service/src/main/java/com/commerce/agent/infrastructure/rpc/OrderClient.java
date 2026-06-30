package com.commerce.agent.infrastructure.rpc;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commerce-order-service")
public interface OrderClient {

	@GetMapping("/api/orders")
	List<Map<String, Object>> query(
			@RequestParam(name = "orderId", required = false) Long orderId,
			@RequestParam(name = "userId", required = false) Long userId,
			@RequestParam(name = "status", required = false) String status
	);

	@GetMapping("/api/orders/{orderId}")
	Map<String, Object> get(@PathVariable Long orderId);
}

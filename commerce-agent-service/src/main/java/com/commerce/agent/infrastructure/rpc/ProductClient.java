package com.commerce.agent.infrastructure.rpc;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commerce-product-service")
public interface ProductClient {

	@GetMapping("/api/products")
	List<Map<String, Object>> query(
			@RequestParam(name = "id", required = false) Long id,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "skuId", required = false) Long skuId,
			@RequestParam(name = "enabled", required = false) Boolean enabled,
			@RequestParam(name = "deleted", required = false) Boolean deleted
	);

	@GetMapping("/api/products/{productId}")
	Map<String, Object> get(@PathVariable Long productId);
}

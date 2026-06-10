package com.commerce.order.infrastructure.rpc;

import com.commerce.order.application.port.ProductQueryPort;
import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "commerce-product-service")
public interface ProductClient extends ProductQueryPort {

	@Override
	@GetMapping("/internal/products/{productId}/price")
	BigDecimal getPrice(@PathVariable("productId") Long productId);

}

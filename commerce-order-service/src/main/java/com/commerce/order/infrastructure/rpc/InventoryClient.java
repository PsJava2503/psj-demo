package com.commerce.order.infrastructure.rpc;

import com.commerce.order.application.port.InventoryCommandPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commerce-inventory-service")
public interface InventoryClient extends InventoryCommandPort {

	@Override
	@PostMapping("/internal/inventory/deduct")
	boolean deductStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);

}

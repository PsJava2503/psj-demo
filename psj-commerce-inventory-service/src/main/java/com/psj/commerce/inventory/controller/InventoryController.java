package com.psj.commerce.inventory.controller;

import com.psj.commerce.api.InventoryRpcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	private final InventoryRpcService inventoryRpcService;

	public InventoryController(InventoryRpcService inventoryRpcService) {
		this.inventoryRpcService = inventoryRpcService;
	}

	@GetMapping("/{productId}/available")
	public boolean available(@PathVariable Long productId) {
		return inventoryRpcService.deductStock(productId, 1);
	}

}

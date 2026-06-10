package com.commerce.inventory.interfaces.rest;

import com.commerce.inventory.application.port.InventoryUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	private final InventoryUseCase inventoryUseCase;

	public InventoryController(InventoryUseCase inventoryUseCase) {
		this.inventoryUseCase = inventoryUseCase;
	}

	@GetMapping("/{productId}/available")
	public boolean available(@PathVariable Long productId) {
		return inventoryUseCase.available(productId);
	}

	@PostMapping("/deduct")
	public boolean deduct(@RequestParam Long productId, @RequestParam Integer quantity) {
		return inventoryUseCase.deductStock(productId, quantity);
	}

}

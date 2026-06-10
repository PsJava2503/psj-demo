package com.psj.commerce.inventory.interfaces.rest;

import com.psj.commerce.inventory.application.port.InventoryUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/inventory")
public class InternalInventoryController {

	private final InventoryUseCase inventoryUseCase;

	public InternalInventoryController(InventoryUseCase inventoryUseCase) {
		this.inventoryUseCase = inventoryUseCase;
	}

	@PostMapping("/deduct")
	public boolean deduct(@RequestParam Long productId, @RequestParam Integer quantity) {
		return inventoryUseCase.deductStock(productId, quantity);
	}
}

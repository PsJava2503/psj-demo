package com.psj.commerce.inventory.interfaces.rest;

import com.psj.commerce.inventory.application.port.InventoryUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

}

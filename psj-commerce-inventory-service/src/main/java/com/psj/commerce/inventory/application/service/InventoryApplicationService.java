package com.psj.commerce.inventory.application.service;

import com.psj.commerce.inventory.application.port.InventoryUseCase;
import com.psj.commerce.inventory.domain.service.InventoryDomainService;
import org.springframework.stereotype.Service;

@Service
public class InventoryApplicationService implements InventoryUseCase {

	private final InventoryDomainService inventoryDomainService;

	public InventoryApplicationService(InventoryDomainService inventoryDomainService) {
		this.inventoryDomainService = inventoryDomainService;
	}

	@Override
	public boolean deductStock(Long productId, Integer quantity) {
		return inventoryDomainService.canDeduct(productId, quantity);
	}

	@Override
	public boolean available(Long productId) {
		return inventoryDomainService.canDeduct(productId, 1);
	}

}

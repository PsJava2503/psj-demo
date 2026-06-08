package com.psj.commerce.inventory.application.port;

public interface InventoryUseCase {

	boolean deductStock(Long productId, Integer quantity);

	boolean available(Long productId);

}

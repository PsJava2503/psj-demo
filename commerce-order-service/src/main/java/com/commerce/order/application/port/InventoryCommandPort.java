package com.commerce.order.application.port;

public interface InventoryCommandPort {

	boolean deductStock(Long productId, Integer quantity);

}

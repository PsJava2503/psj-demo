package com.psj.commerce.api;

public interface InventoryRpcService {

	boolean deductStock(Long productId, Integer quantity);

}

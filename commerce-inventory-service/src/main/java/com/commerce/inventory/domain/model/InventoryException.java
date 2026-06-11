package com.commerce.inventory.domain.model;

public class InventoryException extends RuntimeException {

	public InventoryException(String message) {
		super(message);
	}

	public static InventoryException insufficientStock(Long skuId, Long warehouseId, long requested, long available) {
		return new InventoryException("insufficient stock: skuId=%s, warehouseId=%s, requested=%d, available=%d"
			.formatted(skuId, warehouseId, requested, available));
	}

	public static InventoryException insufficientBinStock(Long skuId, Long binId, long requested, long available) {
		return new InventoryException("insufficient bin stock: skuId=%s, binId=%s, requested=%d, available=%d"
			.formatted(skuId, binId, requested, available));
	}

	public static InventoryException versionConflict(Long id) {
		return new InventoryException("version conflict: " + id);
	}
}

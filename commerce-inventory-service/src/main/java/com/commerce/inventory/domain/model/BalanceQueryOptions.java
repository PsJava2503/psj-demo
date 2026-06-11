package com.commerce.inventory.domain.model;

import java.util.Optional;

public class BalanceQueryOptions {

	private final Optional<Long> skuId;
	private final Optional<Long> warehouseId;

	public BalanceQueryOptions(Optional<Long> skuId, Optional<Long> warehouseId) {
		this.skuId = skuId == null ? Optional.empty() : skuId;
		this.warehouseId = warehouseId == null ? Optional.empty() : warehouseId;
	}

	public static BalanceQueryOptions none() {
		return new BalanceQueryOptions(Optional.empty(), Optional.empty());
	}

	public static BalanceQueryOptions bySkuAndWarehouse(Long skuId, Long warehouseId) {
		return new BalanceQueryOptions(Optional.ofNullable(skuId), Optional.ofNullable(warehouseId));
	}

	public Long getSkuIdValue() {
		return skuId.orElse(null);
	}

	public Long getWarehouseIdValue() {
		return warehouseId.orElse(null);
	}
}

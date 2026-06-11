package com.commerce.inventory.domain.model;

import java.util.Optional;

public class BinBalanceQueryOptions {

	private final Optional<Long> skuId;
	private final Optional<Long> binId;

	public BinBalanceQueryOptions(Optional<Long> skuId, Optional<Long> binId) {
		this.skuId = skuId == null ? Optional.empty() : skuId;
		this.binId = binId == null ? Optional.empty() : binId;
	}

	public static BinBalanceQueryOptions none() {
		return new BinBalanceQueryOptions(Optional.empty(), Optional.empty());
	}

	public static BinBalanceQueryOptions bySkuAndBin(Long skuId, Long binId) {
		return new BinBalanceQueryOptions(Optional.ofNullable(skuId), Optional.ofNullable(binId));
	}

	public Long getSkuIdValue() {
		return skuId.orElse(null);
	}

	public Long getBinIdValue() {
		return binId.orElse(null);
	}
}

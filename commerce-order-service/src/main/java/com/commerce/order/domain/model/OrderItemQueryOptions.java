package com.commerce.order.domain.model;

import java.util.Optional;

public class OrderItemQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> orderId;
	private final Optional<Long> productId;
	private final Optional<Long> skuId;

	public OrderItemQueryOptions(
			Optional<Long> id,
			Optional<Long> orderId,
			Optional<Long> productId,
			Optional<Long> skuId
	) {
		this.id = id == null ? Optional.empty() : id;
		this.orderId = orderId == null ? Optional.empty() : orderId;
		this.productId = productId == null ? Optional.empty() : productId;
		this.skuId = skuId == null ? Optional.empty() : skuId;
	}

	public static OrderItemQueryOptions none() {
		return new OrderItemQueryOptions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public Long getOrderIdValue() {
		return orderId.orElse(null);
	}

	public Long getProductIdValue() {
		return productId.orElse(null);
	}

	public Long getSkuIdValue() {
		return skuId.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent() || orderId.isPresent() || productId.isPresent() || skuId.isPresent();
	}
}

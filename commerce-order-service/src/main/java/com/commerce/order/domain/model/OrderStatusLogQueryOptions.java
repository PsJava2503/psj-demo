package com.commerce.order.domain.model;

import java.util.Optional;

public class OrderStatusLogQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> orderId;
	private final Optional<OrderStatus> toStatus;

	public OrderStatusLogQueryOptions(Optional<Long> id, Optional<Long> orderId, Optional<OrderStatus> toStatus) {
		this.id = id == null ? Optional.empty() : id;
		this.orderId = orderId == null ? Optional.empty() : orderId;
		this.toStatus = toStatus == null ? Optional.empty() : toStatus;
	}

	public static OrderStatusLogQueryOptions none() {
		return new OrderStatusLogQueryOptions(Optional.empty(), Optional.empty(), Optional.empty());
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public Long getOrderIdValue() {
		return orderId.orElse(null);
	}

	public String getToStatusValue() {
		return toStatus.map(OrderStatus::name).orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent() || orderId.isPresent() || toStatus.isPresent();
	}
}

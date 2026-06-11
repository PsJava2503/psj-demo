package com.commerce.order.domain.model;

import java.util.Optional;

public class OrderQueryOptions {

	private final Optional<Long> orderId;
	private final Optional<OrderStatus> status;

	public OrderQueryOptions(Optional<Long> orderId, Optional<OrderStatus> status) {
		this.orderId = orderId == null ? Optional.empty() : orderId;
		this.status = status == null ? Optional.empty() : status;
	}

	public static OrderQueryOptions none() {
		return new OrderQueryOptions(Optional.empty(), Optional.empty());
	}

	public Optional<Long> getOrderId() {
		return orderId;
	}

	public Optional<OrderStatus> getStatus() {
		return status;
	}
}

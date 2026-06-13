package com.commerce.order.domain.model;

import java.time.ZonedDateTime;
import java.util.Optional;

public class OrderQueryOptions {

	private final Optional<Long> orderId;
	private final Optional<Long> userId;
	private final Optional<OrderStatus> status;
	private final Optional<ZonedDateTime> createTimeBefore;

	public OrderQueryOptions(
			Optional<Long> orderId,
			Optional<Long> userId,
			Optional<OrderStatus> status,
			Optional<ZonedDateTime> createTimeBefore
	) {
		this.orderId = orderId == null ? Optional.empty() : orderId;
		this.userId = userId == null ? Optional.empty() : userId;
		this.status = status == null ? Optional.empty() : status;
		this.createTimeBefore = createTimeBefore == null ? Optional.empty() : createTimeBefore;
	}

	public static OrderQueryOptions none() {
		return new OrderQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	public Optional<Long> getOrderId() {
		return orderId;
	}

	public Optional<Long> getUserId() {
		return userId;
	}

	public Optional<OrderStatus> getStatus() {
		return status;
	}

	public Optional<ZonedDateTime> getCreateTimeBefore() {
		return createTimeBefore;
	}

	public Long getOrderIdValue() {
		return orderId.orElse(null);
	}

	public Long getUserIdValue() {
		return userId.orElse(null);
	}

	public String getStatusValue() {
		return status.map(OrderStatus::name).orElse(null);
	}

	public ZonedDateTime getCreateTimeBeforeValue() {
		return createTimeBefore.orElse(null);
	}

	public boolean hasConditions() {
		return orderId.isPresent()
				|| userId.isPresent()
				|| status.isPresent()
				|| createTimeBefore.isPresent();
	}
}

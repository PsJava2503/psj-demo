package com.commerce.order.domain.model;

import java.util.Optional;

public class OrderInventoryReservationQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> orderId;
	private final Optional<Long> reservationId;
	private final Optional<String> status;

	public OrderInventoryReservationQueryOptions(
			Optional<Long> id,
			Optional<Long> orderId,
			Optional<Long> reservationId,
			Optional<String> status
	) {
		this.id = id == null ? Optional.empty() : id;
		this.orderId = orderId == null ? Optional.empty() : orderId;
		this.reservationId = reservationId == null ? Optional.empty() : reservationId;
		this.status = status == null ? Optional.empty() : status;
	}

	public static OrderInventoryReservationQueryOptions none() {
		return new OrderInventoryReservationQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public Long getOrderIdValue() {
		return orderId.orElse(null);
	}

	public Long getReservationIdValue() {
		return reservationId.orElse(null);
	}

	public String getStatusValue() {
		return status.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent() || orderId.isPresent() || reservationId.isPresent() || status.isPresent();
	}
}

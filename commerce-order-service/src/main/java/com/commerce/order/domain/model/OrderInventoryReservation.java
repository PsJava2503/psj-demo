package com.commerce.order.domain.model;

import java.time.ZonedDateTime;

public record OrderInventoryReservation(
		Long id,
		Long orderId,
		Long reservationId,
		String status,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

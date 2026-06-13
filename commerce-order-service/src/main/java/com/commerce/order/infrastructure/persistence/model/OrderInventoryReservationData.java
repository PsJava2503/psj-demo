package com.commerce.order.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record OrderInventoryReservationData(
		Long id,
		Long orderId,
		Long reservationId,
		String status,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

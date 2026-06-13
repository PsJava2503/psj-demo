package com.commerce.order.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record OrderStatusLogData(
		Long id,
		Long orderId,
		String fromStatus,
		String toStatus,
		String reason,
		ZonedDateTime createTime
) {
}

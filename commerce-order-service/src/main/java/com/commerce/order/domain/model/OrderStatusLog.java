package com.commerce.order.domain.model;

import java.time.ZonedDateTime;

public record OrderStatusLog(
		Long id,
		Long orderId,
		OrderStatus fromStatus,
		OrderStatus toStatus,
		String reason,
		ZonedDateTime createTime
) {
}

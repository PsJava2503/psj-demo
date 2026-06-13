package com.commerce.order.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record OrderOutboxEventData(
		Long id,
		String eventKey,
		String eventType,
		Long aggregateId,
		String payload,
		String status,
		Integer attemptCount,
		String lastError,
		ZonedDateTime nextRetryTime,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

package com.commerce.payment.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record PaymentOutboxEventData(
		Long id,
		String eventKey,
		String eventType,
		String aggregateType,
		String aggregateId,
		String payload,
		String status,
		Integer attemptCount,
		String lastError,
		ZonedDateTime nextRetryTime,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {}

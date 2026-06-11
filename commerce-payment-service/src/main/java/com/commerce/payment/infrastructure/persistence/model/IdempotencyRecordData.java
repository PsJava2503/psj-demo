package com.commerce.payment.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record IdempotencyRecordData(
		Long id,
		String idempotencyKey,
		ZonedDateTime createTime
) {}

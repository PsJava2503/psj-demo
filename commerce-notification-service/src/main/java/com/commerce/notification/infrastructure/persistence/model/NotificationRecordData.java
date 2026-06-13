package com.commerce.notification.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record NotificationRecordData(
		Long id,
		Long orderId,
		Long userId,
		String templateCode,
		String channel,
		String recipient,
		String payload,
		String status,
		String idempotencyKey,
		ZonedDateTime sentTime,
		String errorMessage,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

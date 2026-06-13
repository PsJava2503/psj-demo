package com.commerce.notification.domain.model;

import java.util.Optional;

public class NotificationRecordQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> orderId;
	private final Optional<String> status;
	private final Optional<String> idempotencyKey;

	public NotificationRecordQueryOptions(
			Optional<Long> id,
			Optional<Long> orderId,
			Optional<String> status,
			Optional<String> idempotencyKey
	) {
		this.id = id == null ? Optional.empty() : id;
		this.orderId = orderId == null ? Optional.empty() : orderId;
		this.status = status == null ? Optional.empty() : status;
		this.idempotencyKey = idempotencyKey == null ? Optional.empty() : idempotencyKey;
	}

	public static NotificationRecordQueryOptions none() {
		return new NotificationRecordQueryOptions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public Long getOrderIdValue() {
		return orderId.orElse(null);
	}

	public String getStatusValue() {
		return status.orElse(null);
	}

	public String getIdempotencyKeyValue() {
		return idempotencyKey.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent() || orderId.isPresent() || status.isPresent() || idempotencyKey.isPresent();
	}
}

package com.commerce.payment.domain.model;

import java.util.Optional;

public class IdempotencyRecordQueryOptions {

	private final Optional<Long> id;
	private final Optional<String> idempotencyKey;

	public IdempotencyRecordQueryOptions(Optional<Long> id, Optional<String> idempotencyKey) {
		this.id = id == null ? Optional.empty() : id;
		this.idempotencyKey = idempotencyKey == null ? Optional.empty() : idempotencyKey;
	}

	public static IdempotencyRecordQueryOptions none() {
		return new IdempotencyRecordQueryOptions(Optional.empty(), Optional.empty());
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public String getIdempotencyKeyValue() {
		return idempotencyKey.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent() || idempotencyKey.isPresent();
	}
}

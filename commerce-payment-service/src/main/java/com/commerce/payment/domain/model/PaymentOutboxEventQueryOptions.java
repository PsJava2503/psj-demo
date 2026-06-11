package com.commerce.payment.domain.model;

import java.time.ZonedDateTime;
import java.util.Optional;

public class PaymentOutboxEventQueryOptions {

	private final Optional<Long> id;
	private final Optional<String> eventKey;
	private final Optional<String> eventType;
	private final Optional<PaymentOutboxEventStatus> status;
	private final Optional<ZonedDateTime> nextRetryTimeBefore;

	public PaymentOutboxEventQueryOptions(
			Optional<Long> id,
			Optional<String> eventKey,
			Optional<String> eventType,
			Optional<PaymentOutboxEventStatus> status,
			Optional<ZonedDateTime> nextRetryTimeBefore
	) {
		this.id = id == null ? Optional.empty() : id;
		this.eventKey = eventKey == null ? Optional.empty() : eventKey;
		this.eventType = eventType == null ? Optional.empty() : eventType;
		this.status = status == null ? Optional.empty() : status;
		this.nextRetryTimeBefore = nextRetryTimeBefore == null ? Optional.empty() : nextRetryTimeBefore;
	}

	public static PaymentOutboxEventQueryOptions none() {
		return new PaymentOutboxEventQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public String getEventKeyValue() {
		return eventKey.orElse(null);
	}

	public String getEventTypeValue() {
		return eventType.orElse(null);
	}

	public String getStatusValue() {
		return status.map(PaymentOutboxEventStatus::name).orElse(null);
	}

	public ZonedDateTime getNextRetryTimeBeforeValue() {
		return nextRetryTimeBefore.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent()
				|| eventKey.isPresent()
				|| eventType.isPresent()
				|| status.isPresent()
				|| nextRetryTimeBefore.isPresent();
	}
}

package com.commerce.order.domain.model;

import java.time.ZonedDateTime;
import java.util.Optional;

public class OrderOutboxEventQueryOptions {

	private final Optional<Long> id;
	private final Optional<String> eventKey;
	private final Optional<String> status;
	private final Optional<ZonedDateTime> nextRetryTimeBefore;

	public OrderOutboxEventQueryOptions(
			Optional<Long> id,
			Optional<String> eventKey,
			Optional<String> status,
			Optional<ZonedDateTime> nextRetryTimeBefore
	) {
		this.id = id == null ? Optional.empty() : id;
		this.eventKey = eventKey == null ? Optional.empty() : eventKey;
		this.status = status == null ? Optional.empty() : status;
		this.nextRetryTimeBefore = nextRetryTimeBefore == null ? Optional.empty() : nextRetryTimeBefore;
	}

	public static OrderOutboxEventQueryOptions none() {
		return new OrderOutboxEventQueryOptions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public String getEventKeyValue() {
		return eventKey.orElse(null);
	}

	public String getStatusValue() {
		return status.orElse(null);
	}

	public ZonedDateTime getNextRetryTimeBeforeValue() {
		return nextRetryTimeBefore.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent() || eventKey.isPresent() || status.isPresent() || nextRetryTimeBefore.isPresent();
	}
}

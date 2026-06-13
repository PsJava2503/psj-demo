package com.commerce.order.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class OrderOutboxEventDynamicSqlSupport {

	public static final EventTable orderOutboxEvents = new EventTable();
	public static final SqlColumn<Long> id = orderOutboxEvents.id;
	public static final SqlColumn<String> eventKey = orderOutboxEvents.eventKey;
	public static final SqlColumn<String> eventType = orderOutboxEvents.eventType;
	public static final SqlColumn<Long> aggregateId = orderOutboxEvents.aggregateId;
	public static final SqlColumn<String> payload = orderOutboxEvents.payload;
	public static final SqlColumn<String> status = orderOutboxEvents.status;
	public static final SqlColumn<Integer> attemptCount = orderOutboxEvents.attemptCount;
	public static final SqlColumn<String> lastError = orderOutboxEvents.lastError;
	public static final SqlColumn<ZonedDateTime> nextRetryTime = orderOutboxEvents.nextRetryTime;
	public static final SqlColumn<ZonedDateTime> createTime = orderOutboxEvents.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = orderOutboxEvents.updateTime;

	private OrderOutboxEventDynamicSqlSupport() {
	}

	public static final class EventTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> eventKey = column("event_key");
		public final SqlColumn<String> eventType = column("event_type");
		public final SqlColumn<Long> aggregateId = column("aggregate_id");
		public final SqlColumn<String> payload = column("payload");
		public final SqlColumn<String> status = column("status");
		public final SqlColumn<Integer> attemptCount = column("attempt_count");
		public final SqlColumn<String> lastError = column("last_error");
		public final SqlColumn<ZonedDateTime> nextRetryTime = column("next_retry_time");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public EventTable() {
			super("order_outbox_events");
		}
	}
}

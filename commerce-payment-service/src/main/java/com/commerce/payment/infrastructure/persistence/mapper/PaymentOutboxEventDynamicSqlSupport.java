package com.commerce.payment.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PaymentOutboxEventDynamicSqlSupport {

	public static final PaymentOutboxEventTable paymentOutboxEvent = new PaymentOutboxEventTable();
	public static final SqlColumn<Long> id = paymentOutboxEvent.id;
	public static final SqlColumn<String> eventKey = paymentOutboxEvent.eventKey;
	public static final SqlColumn<String> eventType = paymentOutboxEvent.eventType;
	public static final SqlColumn<String> aggregateType = paymentOutboxEvent.aggregateType;
	public static final SqlColumn<String> aggregateId = paymentOutboxEvent.aggregateId;
	public static final SqlColumn<String> payload = paymentOutboxEvent.payload;
	public static final SqlColumn<String> status = paymentOutboxEvent.status;
	public static final SqlColumn<Integer> attemptCount = paymentOutboxEvent.attemptCount;
	public static final SqlColumn<String> lastError = paymentOutboxEvent.lastError;
	public static final SqlColumn<ZonedDateTime> nextRetryTime = paymentOutboxEvent.nextRetryTime;
	public static final SqlColumn<ZonedDateTime> createTime = paymentOutboxEvent.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = paymentOutboxEvent.updateTime;

	private PaymentOutboxEventDynamicSqlSupport() {}

	public static final class PaymentOutboxEventTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> eventKey = column("event_key");
		public final SqlColumn<String> eventType = column("event_type");
		public final SqlColumn<String> aggregateType = column("aggregate_type");
		public final SqlColumn<String> aggregateId = column("aggregate_id");
		public final SqlColumn<String> payload = column("payload");
		public final SqlColumn<String> status = column("status");
		public final SqlColumn<Integer> attemptCount = column("attempt_count");
		public final SqlColumn<String> lastError = column("last_error");
		public final SqlColumn<ZonedDateTime> nextRetryTime = column("next_retry_time");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public PaymentOutboxEventTable() {
			super("payment_outbox_event");
		}
	}
}

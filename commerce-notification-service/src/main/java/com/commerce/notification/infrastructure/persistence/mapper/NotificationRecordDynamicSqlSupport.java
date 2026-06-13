package com.commerce.notification.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class NotificationRecordDynamicSqlSupport {

	public static final RecordTable notificationRecords = new RecordTable();
	public static final SqlColumn<Long> id = notificationRecords.id;
	public static final SqlColumn<Long> orderId = notificationRecords.orderId;
	public static final SqlColumn<Long> userId = notificationRecords.userId;
	public static final SqlColumn<String> templateCode = notificationRecords.templateCode;
	public static final SqlColumn<String> channel = notificationRecords.channel;
	public static final SqlColumn<String> recipient = notificationRecords.recipient;
	public static final SqlColumn<String> payload = notificationRecords.payload;
	public static final SqlColumn<String> status = notificationRecords.status;
	public static final SqlColumn<String> idempotencyKey = notificationRecords.idempotencyKey;
	public static final SqlColumn<ZonedDateTime> sentTime = notificationRecords.sentTime;
	public static final SqlColumn<String> errorMessage = notificationRecords.errorMessage;
	public static final SqlColumn<ZonedDateTime> createTime = notificationRecords.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = notificationRecords.updateTime;

	private NotificationRecordDynamicSqlSupport() {
	}

	public static final class RecordTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> orderId = column("order_id");
		public final SqlColumn<Long> userId = column("user_id");
		public final SqlColumn<String> templateCode = column("template_code");
		public final SqlColumn<String> channel = column("channel");
		public final SqlColumn<String> recipient = column("recipient");
		public final SqlColumn<String> payload = column("payload");
		public final SqlColumn<String> status = column("status");
		public final SqlColumn<String> idempotencyKey = column("idempotency_key");
		public final SqlColumn<ZonedDateTime> sentTime = column("sent_time");
		public final SqlColumn<String> errorMessage = column("error_message");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public RecordTable() {
			super("notification_records");
		}
	}
}

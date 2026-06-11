package com.commerce.payment.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class IdempotencyRecordDynamicSqlSupport {

	public static final IdempotencyRecordTable idempotencyRecord = new IdempotencyRecordTable();
	public static final SqlColumn<Long> id = idempotencyRecord.id;
	public static final SqlColumn<String> idempotencyKey = idempotencyRecord.idempotencyKey;
	public static final SqlColumn<ZonedDateTime> createTime = idempotencyRecord.createTime;

	private IdempotencyRecordDynamicSqlSupport() {}

	public static final class IdempotencyRecordTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> idempotencyKey = column("idempotency_key");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");

		public IdempotencyRecordTable() {
			super("idempotency_record");
		}
	}
}

package com.commerce.order.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class OrderStatusLogDynamicSqlSupport {

	public static final OrderStatusLogTable orderStatusLogs = new OrderStatusLogTable();
	public static final SqlColumn<Long> id = orderStatusLogs.id;
	public static final SqlColumn<Long> orderId = orderStatusLogs.orderId;
	public static final SqlColumn<String> fromStatus = orderStatusLogs.fromStatus;
	public static final SqlColumn<String> toStatus = orderStatusLogs.toStatus;
	public static final SqlColumn<String> reason = orderStatusLogs.reason;
	public static final SqlColumn<ZonedDateTime> createTime = orderStatusLogs.createTime;

	private OrderStatusLogDynamicSqlSupport() {
	}

	public static final class OrderStatusLogTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> orderId = column("order_id");
		public final SqlColumn<String> fromStatus = column("from_status");
		public final SqlColumn<String> toStatus = column("to_status");
		public final SqlColumn<String> reason = column("reason");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");

		public OrderStatusLogTable() {
			super("order_status_logs");
		}
	}
}

package com.commerce.order.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class OrderInventoryReservationDynamicSqlSupport {

	public static final ReservationTable orderInventoryReservations = new ReservationTable();
	public static final SqlColumn<Long> id = orderInventoryReservations.id;
	public static final SqlColumn<Long> orderId = orderInventoryReservations.orderId;
	public static final SqlColumn<Long> reservationId = orderInventoryReservations.reservationId;
	public static final SqlColumn<String> status = orderInventoryReservations.status;
	public static final SqlColumn<ZonedDateTime> createTime = orderInventoryReservations.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = orderInventoryReservations.updateTime;

	private OrderInventoryReservationDynamicSqlSupport() {
	}

	public static final class ReservationTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> orderId = column("order_id");
		public final SqlColumn<Long> reservationId = column("reservation_id");
		public final SqlColumn<String> status = column("status");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public ReservationTable() {
			super("order_inventory_reservations");
		}
	}
}

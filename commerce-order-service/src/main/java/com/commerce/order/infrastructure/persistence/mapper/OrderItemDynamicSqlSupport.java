package com.commerce.order.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class OrderItemDynamicSqlSupport {

	public static final OrderItemTable orderItems = new OrderItemTable();
	public static final SqlColumn<Long> id = orderItems.id;
	public static final SqlColumn<Long> orderId = orderItems.orderId;
	public static final SqlColumn<Long> productId = orderItems.productId;
	public static final SqlColumn<Long> skuId = orderItems.skuId;
	public static final SqlColumn<String> productName = orderItems.productName;
	public static final SqlColumn<BigDecimal> unitPrice = orderItems.unitPrice;
	public static final SqlColumn<Integer> quantity = orderItems.quantity;
	public static final SqlColumn<BigDecimal> amount = orderItems.amount;
	public static final SqlColumn<ZonedDateTime> createTime = orderItems.createTime;

	private OrderItemDynamicSqlSupport() {
	}

	public static final class OrderItemTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> orderId = column("order_id");
		public final SqlColumn<Long> productId = column("product_id");
		public final SqlColumn<Long> skuId = column("sku_id");
		public final SqlColumn<String> productName = column("product_name");
		public final SqlColumn<BigDecimal> unitPrice = column("unit_price");
		public final SqlColumn<Integer> quantity = column("quantity");
		public final SqlColumn<BigDecimal> amount = column("amount");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");

		public OrderItemTable() {
			super("order_items");
		}
	}
}

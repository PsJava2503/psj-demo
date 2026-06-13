package com.commerce.order.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class OrderDynamicSqlSupport {

	public static final OrderTable orders = new OrderTable();
	public static final SqlColumn<Long> id = orders.id;
	public static final SqlColumn<String> orderNo = orders.orderNo;
	public static final SqlColumn<Long> userId = orders.userId;
	public static final SqlColumn<Long> productId = orders.productId;
	public static final SqlColumn<Long> skuId = orders.skuId;
	public static final SqlColumn<String> productName = orders.productName;
	public static final SqlColumn<BigDecimal> unitPrice = orders.unitPrice;
	public static final SqlColumn<Integer> quantity = orders.quantity;
	public static final SqlColumn<BigDecimal> amount = orders.amount;
	public static final SqlColumn<Long> addressId = orders.addressId;
	public static final SqlColumn<String> recipientName = orders.recipientName;
	public static final SqlColumn<String> recipientPhone = orders.recipientPhone;
	public static final SqlColumn<String> province = orders.province;
	public static final SqlColumn<String> city = orders.city;
	public static final SqlColumn<String> district = orders.district;
	public static final SqlColumn<String> addressDetail = orders.addressDetail;
	public static final SqlColumn<String> status = orders.status;
	public static final SqlColumn<Long> version = orders.version;
	public static final SqlColumn<ZonedDateTime> createTime = orders.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = orders.updateTime;

	private OrderDynamicSqlSupport() {
	}

	public static final class OrderTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> orderNo = column("order_no");
		public final SqlColumn<Long> userId = column("user_id");
		public final SqlColumn<Long> productId = column("product_id");
		public final SqlColumn<Long> skuId = column("sku_id");
		public final SqlColumn<String> productName = column("product_name");
		public final SqlColumn<BigDecimal> unitPrice = column("unit_price");
		public final SqlColumn<Integer> quantity = column("quantity");
		public final SqlColumn<BigDecimal> amount = column("amount");
		public final SqlColumn<Long> addressId = column("address_id");
		public final SqlColumn<String> recipientName = column("recipient_name");
		public final SqlColumn<String> recipientPhone = column("recipient_phone");
		public final SqlColumn<String> province = column("province");
		public final SqlColumn<String> city = column("city");
		public final SqlColumn<String> district = column("district");
		public final SqlColumn<String> addressDetail = column("address_detail");
		public final SqlColumn<String> status = column("status");
		public final SqlColumn<Long> version = column("version");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public OrderTable() {
			super("orders");
		}
	}
}

package com.commerce.cart.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class CartItemDynamicSqlSupport {

	public static final CartItemTable cartItems = new CartItemTable();
	public static final SqlColumn<Long> id = cartItems.id;
	public static final SqlColumn<Long> userId = cartItems.userId;
	public static final SqlColumn<Long> productId = cartItems.productId;
	public static final SqlColumn<Long> skuId = cartItems.skuId;
	public static final SqlColumn<String> productName = cartItems.productName;
	public static final SqlColumn<BigDecimal> unitPrice = cartItems.unitPrice;
	public static final SqlColumn<Integer> quantity = cartItems.quantity;
	public static final SqlColumn<Boolean> selected = cartItems.selected;
	public static final SqlColumn<Boolean> deleted = cartItems.deleted;
	public static final SqlColumn<ZonedDateTime> createTime = cartItems.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = cartItems.updateTime;

	private CartItemDynamicSqlSupport() {
	}

	public static final class CartItemTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> userId = column("user_id");
		public final SqlColumn<Long> productId = column("product_id");
		public final SqlColumn<Long> skuId = column("sku_id");
		public final SqlColumn<String> productName = column("product_name");
		public final SqlColumn<BigDecimal> unitPrice = column("unit_price");
		public final SqlColumn<Integer> quantity = column("quantity");
		public final SqlColumn<Boolean> selected = column("selected");
		public final SqlColumn<Boolean> deleted = column("deleted");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public CartItemTable() {
			super("cart_items");
		}
	}
}

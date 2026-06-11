package com.commerce.payment.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PaymentAllocationDynamicSqlSupport {

	public static final PaymentAllocationTable paymentAllocation = new PaymentAllocationTable();
	public static final SqlColumn<Long> id = paymentAllocation.id;
	public static final SqlColumn<Long> paymentOrderId = paymentAllocation.paymentOrderId;
	public static final SqlColumn<Long> checkoutOrderId = paymentAllocation.checkoutOrderId;
	public static final SqlColumn<Long> subOrderId = paymentAllocation.subOrderId;
	public static final SqlColumn<Long> merchantId = paymentAllocation.merchantId;
	public static final SqlColumn<BigDecimal> goodsAmount = paymentAllocation.goodsAmount;
	public static final SqlColumn<BigDecimal> shippingAmount = paymentAllocation.shippingAmount;
	public static final SqlColumn<BigDecimal> platformDiscountAmount = paymentAllocation.platformDiscountAmount;
	public static final SqlColumn<BigDecimal> merchantDiscountAmount = paymentAllocation.merchantDiscountAmount;
	public static final SqlColumn<BigDecimal> paidAmount = paymentAllocation.paidAmount;
	public static final SqlColumn<BigDecimal> settleAmount = paymentAllocation.settleAmount;
	public static final SqlColumn<ZonedDateTime> createTime = paymentAllocation.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = paymentAllocation.updateTime;

	private PaymentAllocationDynamicSqlSupport() {}

	public static final class PaymentAllocationTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> paymentOrderId = column("payment_order_id");
		public final SqlColumn<Long> checkoutOrderId = column("checkout_order_id");
		public final SqlColumn<Long> subOrderId = column("sub_order_id");
		public final SqlColumn<Long> merchantId = column("merchant_id");
		public final SqlColumn<BigDecimal> goodsAmount = column("goods_amount");
		public final SqlColumn<BigDecimal> shippingAmount = column("shipping_amount");
		public final SqlColumn<BigDecimal> platformDiscountAmount = column("platform_discount_amount");
		public final SqlColumn<BigDecimal> merchantDiscountAmount = column("merchant_discount_amount");
		public final SqlColumn<BigDecimal> paidAmount = column("paid_amount");
		public final SqlColumn<BigDecimal> settleAmount = column("settle_amount");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public PaymentAllocationTable() {
			super("payment_allocation");
		}
	}
}

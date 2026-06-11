package com.commerce.payment.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PaymentRefundAllocationDynamicSqlSupport {

	public static final PaymentRefundAllocationTable paymentRefundAllocation = new PaymentRefundAllocationTable();
	public static final SqlColumn<Long> id = paymentRefundAllocation.id;
	public static final SqlColumn<Long> paymentRefundId = paymentRefundAllocation.paymentRefundId;
	public static final SqlColumn<Long> paymentAllocationId = paymentRefundAllocation.paymentAllocationId;
	public static final SqlColumn<Long> checkoutOrderId = paymentRefundAllocation.checkoutOrderId;
	public static final SqlColumn<Long> subOrderId = paymentRefundAllocation.subOrderId;
	public static final SqlColumn<Long> merchantId = paymentRefundAllocation.merchantId;
	public static final SqlColumn<BigDecimal> refundGoodsAmount = paymentRefundAllocation.refundGoodsAmount;
	public static final SqlColumn<BigDecimal> refundShippingAmount = paymentRefundAllocation.refundShippingAmount;
	public static final SqlColumn<BigDecimal> refundPlatformDiscountAmount = paymentRefundAllocation.refundPlatformDiscountAmount;
	public static final SqlColumn<BigDecimal> refundMerchantDiscountAmount = paymentRefundAllocation.refundMerchantDiscountAmount;
	public static final SqlColumn<BigDecimal> refundPaidAmount = paymentRefundAllocation.refundPaidAmount;
	public static final SqlColumn<ZonedDateTime> createTime = paymentRefundAllocation.createTime;

	private PaymentRefundAllocationDynamicSqlSupport() {}

	public static final class PaymentRefundAllocationTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> paymentRefundId = column("payment_refund_id");
		public final SqlColumn<Long> paymentAllocationId = column("payment_allocation_id");
		public final SqlColumn<Long> checkoutOrderId = column("checkout_order_id");
		public final SqlColumn<Long> subOrderId = column("sub_order_id");
		public final SqlColumn<Long> merchantId = column("merchant_id");
		public final SqlColumn<BigDecimal> refundGoodsAmount = column("refund_goods_amount");
		public final SqlColumn<BigDecimal> refundShippingAmount = column("refund_shipping_amount");
		public final SqlColumn<BigDecimal> refundPlatformDiscountAmount = column("refund_platform_discount_amount");
		public final SqlColumn<BigDecimal> refundMerchantDiscountAmount = column("refund_merchant_discount_amount");
		public final SqlColumn<BigDecimal> refundPaidAmount = column("refund_paid_amount");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");

		public PaymentRefundAllocationTable() {
			super("payment_refund_allocation");
		}
	}
}

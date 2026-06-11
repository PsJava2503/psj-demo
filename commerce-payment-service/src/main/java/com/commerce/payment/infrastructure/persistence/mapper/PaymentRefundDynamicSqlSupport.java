package com.commerce.payment.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PaymentRefundDynamicSqlSupport {

	public static final PaymentRefundTable paymentRefund = new PaymentRefundTable();
	public static final SqlColumn<Long> id = paymentRefund.id;
	public static final SqlColumn<Long> paymentOrderId = paymentRefund.paymentOrderId;
	public static final SqlColumn<Long> checkoutOrderId = paymentRefund.checkoutOrderId;
	public static final SqlColumn<String> outTradeNo = paymentRefund.outTradeNo;
	public static final SqlColumn<String> outRefundNo = paymentRefund.outRefundNo;
	public static final SqlColumn<BigDecimal> refundAmount = paymentRefund.refundAmount;
	public static final SqlColumn<String> status = paymentRefund.status;
	public static final SqlColumn<String> rawResponse = paymentRefund.rawResponse;
	public static final SqlColumn<ZonedDateTime> createTime = paymentRefund.createTime;

	private PaymentRefundDynamicSqlSupport() {}

	public static final class PaymentRefundTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> paymentOrderId = column("payment_order_id");
		public final SqlColumn<Long> checkoutOrderId = column("checkout_order_id");
		public final SqlColumn<String> outTradeNo = column("out_trade_no");
		public final SqlColumn<String> outRefundNo = column("out_refund_no");
		public final SqlColumn<BigDecimal> refundAmount = column("refund_amount");
		public final SqlColumn<String> status = column("status");
		public final SqlColumn<String> rawResponse = column("raw_response");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");

		public PaymentRefundTable() {
			super("payment_refund");
		}
	}
}

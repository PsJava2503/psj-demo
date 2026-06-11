package com.commerce.payment.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PaymentOrderDynamicSqlSupport {

	public static final PaymentOrderTable paymentOrder = new PaymentOrderTable();
	public static final SqlColumn<Long> id = paymentOrder.id;
	public static final SqlColumn<Long> checkoutOrderId = paymentOrder.checkoutOrderId;
	public static final SqlColumn<String> outTradeNo = paymentOrder.outTradeNo;
	public static final SqlColumn<String> tradeNo = paymentOrder.tradeNo;
	public static final SqlColumn<BigDecimal> amount = paymentOrder.amount;
	public static final SqlColumn<String> subject = paymentOrder.subject;
	public static final SqlColumn<String> status = paymentOrder.status;
	public static final SqlColumn<String> rawResponse = paymentOrder.rawResponse;
	public static final SqlColumn<ZonedDateTime> createTime = paymentOrder.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = paymentOrder.updateTime;

	private PaymentOrderDynamicSqlSupport() {}

	public static final class PaymentOrderTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> checkoutOrderId = column("checkout_order_id");
		public final SqlColumn<String> outTradeNo = column("out_trade_no");
		public final SqlColumn<String> tradeNo = column("trade_no");
		public final SqlColumn<BigDecimal> amount = column("amount");
		public final SqlColumn<String> subject = column("subject");
		public final SqlColumn<String> status = column("status");
		public final SqlColumn<String> rawResponse = column("raw_response");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public PaymentOrderTable() {
			super("payment_order");
		}
	}
}

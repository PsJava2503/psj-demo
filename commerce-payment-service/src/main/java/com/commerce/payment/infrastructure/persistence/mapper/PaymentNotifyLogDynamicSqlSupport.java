package com.commerce.payment.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PaymentNotifyLogDynamicSqlSupport {

	public static final PaymentNotifyLogTable paymentNotifyLog = new PaymentNotifyLogTable();
	public static final SqlColumn<Long> id = paymentNotifyLog.id;
	public static final SqlColumn<String> notifyId = paymentNotifyLog.notifyId;
	public static final SqlColumn<String> outTradeNo = paymentNotifyLog.outTradeNo;
	public static final SqlColumn<String> tradeStatus = paymentNotifyLog.tradeStatus;
	public static final SqlColumn<Boolean> verified = paymentNotifyLog.verified;
	public static final SqlColumn<String> payload = paymentNotifyLog.payload;
	public static final SqlColumn<ZonedDateTime> createTime = paymentNotifyLog.createTime;

	private PaymentNotifyLogDynamicSqlSupport() {}

	public static final class PaymentNotifyLogTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> notifyId = column("notify_id");
		public final SqlColumn<String> outTradeNo = column("out_trade_no");
		public final SqlColumn<String> tradeStatus = column("trade_status");
		public final SqlColumn<Boolean> verified = column("verified");
		public final SqlColumn<String> payload = column("payload");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");

		public PaymentNotifyLogTable() {
			super("payment_notify_log");
		}
	}
}

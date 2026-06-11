package com.commerce.payment.infrastructure.persistence.mapper;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PaymentReconcileRecordDynamicSqlSupport {

	public static final PaymentReconcileRecordTable paymentReconcileRecord = new PaymentReconcileRecordTable();
	public static final SqlColumn<Long> id = paymentReconcileRecord.id;
	public static final SqlColumn<LocalDate> billDate = paymentReconcileRecord.billDate;
	public static final SqlColumn<String> billType = paymentReconcileRecord.billType;
	public static final SqlColumn<String> downloadUrl = paymentReconcileRecord.downloadUrl;
	public static final SqlColumn<String> status = paymentReconcileRecord.status;
	public static final SqlColumn<String> rawResponse = paymentReconcileRecord.rawResponse;
	public static final SqlColumn<ZonedDateTime> createTime = paymentReconcileRecord.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = paymentReconcileRecord.updateTime;

	private PaymentReconcileRecordDynamicSqlSupport() {}

	public static final class PaymentReconcileRecordTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<LocalDate> billDate = column("bill_date");
		public final SqlColumn<String> billType = column("bill_type");
		public final SqlColumn<String> downloadUrl = column("download_url");
		public final SqlColumn<String> status = column("status");
		public final SqlColumn<String> rawResponse = column("raw_response");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public PaymentReconcileRecordTable() {
			super("payment_reconcile_record");
		}
	}
}

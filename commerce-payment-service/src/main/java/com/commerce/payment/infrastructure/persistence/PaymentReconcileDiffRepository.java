package com.commerce.payment.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentReconcileDiffRepository {

	private final JdbcTemplate jdbcTemplate;

	public PaymentReconcileDiffRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int deleteByBill(LocalDate billDate, String billType) {
		return jdbcTemplate.update("DELETE FROM payment_reconcile_diff WHERE bill_date = ? AND bill_type = ?", billDate, billType);
	}

	public int create(
			LocalDate billDate,
			String billType,
			String diffType,
			String outTradeNo,
			String outRefundNo,
			String localStatus,
			String channelStatus,
			BigDecimal localAmount,
			BigDecimal channelAmount,
			String detail
	) {
		return jdbcTemplate.update("""
				INSERT INTO payment_reconcile_diff (
				    bill_date, bill_type, diff_type, out_trade_no, out_refund_no, local_status, channel_status,
				    local_amount, channel_amount, detail
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""",
				billDate,
				billType,
				diffType,
				outTradeNo,
				outRefundNo,
				localStatus,
				channelStatus,
				localAmount,
				channelAmount,
				detail);
	}
}

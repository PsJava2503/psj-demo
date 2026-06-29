package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.application.service.reconcile.PaymentBillLine;
import java.time.LocalDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentReconcileDetailRepository {

	private final JdbcTemplate jdbcTemplate;

	public PaymentReconcileDetailRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int deleteByBill(LocalDate billDate, String billType) {
		return jdbcTemplate.update("DELETE FROM payment_reconcile_detail WHERE bill_date = ? AND bill_type = ?", billDate, billType);
	}

	public int create(LocalDate billDate, String billType, PaymentBillLine line) {
		return jdbcTemplate.update("""
				INSERT INTO payment_reconcile_detail (
				    bill_date, bill_type, out_trade_no, trade_no, out_refund_no, amount, trade_status, raw_line
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""",
				billDate,
				billType,
				line.outTradeNo(),
				line.tradeNo(),
				line.outRefundNo(),
				line.amount(),
				line.tradeStatus(),
				line.rawLine());
	}
}

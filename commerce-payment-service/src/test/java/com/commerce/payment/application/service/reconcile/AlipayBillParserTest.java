package com.commerce.payment.application.service.reconcile;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AlipayBillParserTest {

	private final AlipayBillParser parser = new AlipayBillParser();

	@Test
	void parsesPaymentAndRefundLines() {
		String bill = """
				out_trade_no,trade_no,out_refund_no,total_amount,trade_status
				T100,20260001,,99.00,TRADE_SUCCESS
				T100,20260001,R100,20.00,REFUND_SUCCESS
				""";

		var lines = parser.parse(bill);

		assertThat(lines).hasSize(2);
		assertThat(lines.get(0).outTradeNo()).isEqualTo("T100");
		assertThat(lines.get(0).amount()).isEqualByComparingTo(new BigDecimal("99.00"));
		assertThat(lines.get(1).outRefundNo()).isEqualTo("R100");
		assertThat(lines.get(1).amount()).isEqualByComparingTo(new BigDecimal("20.00"));
	}
}

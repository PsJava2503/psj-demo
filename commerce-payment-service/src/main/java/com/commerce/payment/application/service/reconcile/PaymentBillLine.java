package com.commerce.payment.application.service.reconcile;

import java.math.BigDecimal;

public record PaymentBillLine(
		String outTradeNo,
		String tradeNo,
		String outRefundNo,
		BigDecimal amount,
		String tradeStatus,
		String rawLine
) {
}

package com.commerce.payment.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PaymentRefundData(
		Long id,
		Long paymentOrderId,
		Long checkoutOrderId,
		String outTradeNo,
		String outRefundNo,
		BigDecimal refundAmount,
		String status,
		String rawResponse,
		ZonedDateTime createTime
) {}

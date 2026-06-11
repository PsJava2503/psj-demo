package com.commerce.payment.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PaymentOrderData(
		Long id,
		Long checkoutOrderId,
		String outTradeNo,
		String tradeNo,
		BigDecimal amount,
		String subject,
		String status,
		String rawResponse,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {}

package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.PaymentOrderStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PaymentOrderEntity(
		Long id,
		Long checkoutOrderId,
		String outTradeNo,
		String tradeNo,
		BigDecimal amount,
		String subject,
		PaymentOrderStatus status,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {}

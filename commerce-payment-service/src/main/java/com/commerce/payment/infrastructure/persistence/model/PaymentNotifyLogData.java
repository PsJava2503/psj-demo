package com.commerce.payment.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record PaymentNotifyLogData(
		Long id,
		String notifyId,
		String outTradeNo,
		String tradeStatus,
		Boolean verified,
		String payload,
		ZonedDateTime createTime
) {}

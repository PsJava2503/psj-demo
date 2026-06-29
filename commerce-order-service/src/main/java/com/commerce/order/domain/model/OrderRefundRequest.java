package com.commerce.order.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OrderRefundRequest(
		Long id,
		String refundRequestNo,
		Long checkoutOrderId,
		Long subOrderId,
		BigDecimal refundAmount,
		String reason,
		String status,
		String rawResponse,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

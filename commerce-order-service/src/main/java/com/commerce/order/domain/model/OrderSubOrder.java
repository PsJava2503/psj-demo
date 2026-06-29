package com.commerce.order.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OrderSubOrder(
		Long id,
		Long checkoutOrderId,
		Long productId,
		Long skuId,
		String productName,
		BigDecimal unitPrice,
		Integer quantity,
		BigDecimal amount,
		Long merchantId,
		String status,
		String refundStatus,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

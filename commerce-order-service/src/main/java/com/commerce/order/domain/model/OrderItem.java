package com.commerce.order.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OrderItem(
		Long id,
		Long orderId,
		Long productId,
		Long skuId,
		String productName,
		BigDecimal unitPrice,
		Integer quantity,
		BigDecimal amount,
		ZonedDateTime createTime
) {
}

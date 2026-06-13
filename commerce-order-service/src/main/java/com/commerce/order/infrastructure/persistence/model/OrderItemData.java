package com.commerce.order.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OrderItemData(
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

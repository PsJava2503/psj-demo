package com.commerce.cart.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record CartItem(
		Long id,
		Long userId,
		Long productId,
		Long skuId,
		String productName,
		BigDecimal unitPrice,
		Integer quantity,
		Boolean selected,
		Boolean deleted,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

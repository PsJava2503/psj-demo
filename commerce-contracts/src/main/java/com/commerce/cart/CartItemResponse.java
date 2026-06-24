package com.commerce.cart;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record CartItemResponse(
		Long id,
		Long userId,
		Long productId,
		Long skuId,
		String productName,
		BigDecimal unitPrice,
		Integer quantity,
		Boolean selected,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

package com.commerce.cart.interfaces.rest;

import java.math.BigDecimal;

public record AddCartItemRequest(
		Long userId,
		Long productId,
		Long skuId,
		String productName,
		BigDecimal unitPrice,
		Integer quantity
) {
}

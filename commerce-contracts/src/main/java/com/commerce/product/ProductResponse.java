package com.commerce.product;

import java.math.BigDecimal;

public record ProductResponse(
		Long id,
		String name,
		BigDecimal price,
		Long skuId,
		Boolean enabled
) {
}

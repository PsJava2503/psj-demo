package com.commerce.product.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record Product(
		Long id,
		String name,
		BigDecimal price,
		Long skuId,
		Boolean enabled,
		Boolean deleted,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

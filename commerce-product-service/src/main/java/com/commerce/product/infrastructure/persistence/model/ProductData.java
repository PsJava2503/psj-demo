package com.commerce.product.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ProductData(
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

package com.commerce.order.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OrderData(
		Long id,
		String orderNo,
		Long userId,
		Long productId,
		Long skuId,
		String productName,
		BigDecimal unitPrice,
		Integer quantity,
		BigDecimal amount,
		Long addressId,
		String recipientName,
		String recipientPhone,
		String province,
		String city,
		String district,
		String addressDetail,
		String status,
		Long version,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

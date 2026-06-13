package com.commerce.address.domain.model;

import java.time.ZonedDateTime;

public record Address(
		Long id,
		Long userId,
		String recipientName,
		String phone,
		String province,
		String city,
		String district,
		String detail,
		Boolean defaultAddress,
		Boolean deleted,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

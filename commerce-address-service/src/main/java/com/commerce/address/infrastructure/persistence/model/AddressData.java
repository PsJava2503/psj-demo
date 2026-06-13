package com.commerce.address.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record AddressData(
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

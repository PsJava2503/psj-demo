package com.commerce.user.domain.model;

import java.time.ZonedDateTime;

public record UserAddressSlot(
		Long id,
		Long userId,
		Long addressId,
		String slotName,
		Boolean deleted,
		ZonedDateTime createTime
) {
}

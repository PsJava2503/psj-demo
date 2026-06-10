package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record UserAddressSlotData(
		Long id,
		Long userId,
		Long addressId,
		String slotName,
		Boolean deleted,
		ZonedDateTime createTime
) {
}

package com.commerce.user;

import java.time.ZonedDateTime;

public record UserAddressSlotResponse(
		Long id,
		Long userId,
		Long addressId,
		String slotName,
		Boolean deleted,
		ZonedDateTime createTime
) {
}

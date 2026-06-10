package com.commerce.user.domain.model;

import java.time.ZonedDateTime;
import java.util.List;

public record User(
		Long id,
		String firstName,
		String secondName,
		String phone,
		String email,
		Long defaultAddressSlotId,
		List<String> roles,
		List<UserAddressSlot> addressSlots,
		Boolean enabled,
		Boolean deleted,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

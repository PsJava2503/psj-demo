package com.commerce.user;

import java.time.ZonedDateTime;
import java.util.List;

public record UserResponse(
		Long id,
		String firstName,
		String secondName,
		String phone,
		String email,
		Long defaultAddressSlotId,
		List<String> roles,
		List<UserAddressSlotResponse> addressSlots,
		Boolean enabled,
		Boolean deleted,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

package com.commerce.user;

import java.time.ZonedDateTime;

public record UserResponse(
		Long id,
		String firstName,
		String secondName,
		String phone,
		String email,
		Long defaultAddressId,
		Boolean deleted,
		ZonedDateTime createTime
) {
}

package com.commerce.user.domain.model;

import java.time.ZonedDateTime;

public record User(
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

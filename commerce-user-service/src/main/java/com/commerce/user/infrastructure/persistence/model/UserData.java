package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record UserData(
		Long id,
		String firstName,
		String secondName,
		String phone,
		String email,
		Long defaultAddressSlotId,
		Boolean deleted,
		Boolean enabled,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

package com.commerce.user.domain.model;

import java.time.ZonedDateTime;

public record UserCredential(
		Long userId,
		String username,
		String passwordHash,
		String passwordSalt,
		String passwordAlgorithm,
		Boolean enabled,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

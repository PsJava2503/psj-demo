package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record UserCredentialData(
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

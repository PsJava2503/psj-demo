package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record RoleData(
		Long id,
		String code,
		String name,
		String description,
		Boolean enabled,
		Boolean deleted,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

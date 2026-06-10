package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record PermissionData(
		Long id,
		String code,
		String resource,
		String action,
		String name,
		String description,
		Boolean enabled,
		Boolean deleted,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {
}

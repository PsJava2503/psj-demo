package com.commerce.user.domain.model;

import java.time.ZonedDateTime;

public record Permission(
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

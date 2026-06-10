package com.commerce.user.domain.model;

import java.time.ZonedDateTime;

public record Role(
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

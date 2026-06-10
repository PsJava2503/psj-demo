package com.commerce.user.domain.model;

import java.util.List;

public record AuthPrincipal(
		Long userId,
		String username,
		List<String> roles,
		List<String> permissions
) {
}

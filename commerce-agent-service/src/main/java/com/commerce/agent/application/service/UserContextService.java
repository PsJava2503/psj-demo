package com.commerce.agent.application.service;

import com.commerce.security.SecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserContextService {

	private static final ThreadLocal<UserContext> CURRENT = new ThreadLocal<>();

	public UserContext from(HttpServletRequest request) {
		UserContext context = new UserContext(
				parseLong(request.getHeader(SecurityHeaders.USER_ID)).orElse(null),
				request.getHeader(SecurityHeaders.USERNAME),
				request.getHeader(SecurityHeaders.ROLES),
				request.getHeader(SecurityHeaders.PERMISSIONS)
		);
		CURRENT.set(context);
		return context;
	}

	public UserContext current() {
		UserContext context = CURRENT.get();
		return context == null ? UserContext.empty() : context;
	}

	public void set(UserContext context) {
		CURRENT.set(context);
	}

	public void clear() {
		CURRENT.remove();
	}

	private Optional<Long> parseLong(String value) {
		if (!StringUtils.hasText(value)) {
			return Optional.empty();
		}
		try {
			return Optional.of(Long.valueOf(value));
		}
		catch (NumberFormatException ignored) {
			return Optional.empty();
		}
	}

	public record UserContext(
			Long userId,
			String username,
			String roles,
			String permissions
	) {

		public static UserContext empty() {
			return new UserContext(null, "", "", "");
		}

		public boolean hasRole(String role) {
			return containsCsvValue(roles, role);
		}

		public boolean hasPermission(String permission) {
			return containsCsvValue(permissions, permission);
		}

		private boolean containsCsvValue(String csv, String expected) {
			if (!StringUtils.hasText(csv) || !StringUtils.hasText(expected)) {
				return false;
			}
			return Arrays.stream(csv.split(","))
					.map(String::trim)
					.anyMatch(expected::equalsIgnoreCase);
		}
	}
}

package com.psj.commerce.user.interfaces.rest;

import com.psj.commerce.security.AuthSession;
import java.util.List;

public record LoginResponse(Long userId, String username, String token, List<String> roles, List<String> permissions) {

	public static LoginResponse of(AuthSession session, String token) {
		return new LoginResponse(session.userId(), session.username(), token, session.roles(), session.permissions());
	}

}

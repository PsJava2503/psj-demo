package com.psj.commerce.user.application.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.session.SaSession;
import com.psj.commerce.security.AuthSession;
import com.psj.commerce.user.application.port.AuthUseCase;
import com.psj.commerce.user.infrastructure.persistence.DemoRbacStore;
import com.psj.commerce.user.infrastructure.persistence.DemoRbacStore.DemoUser;
import com.psj.commerce.user.interfaces.rest.LoginRequest;
import com.psj.commerce.user.interfaces.rest.LoginResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthApplicationService implements AuthUseCase {

	private final DemoRbacStore rbacStore;

	public AuthApplicationService(DemoRbacStore rbacStore) {
		this.rbacStore = rbacStore;
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		DemoUser user = rbacStore.authenticate(request.username(), request.password());
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid username or password");
		}
		AuthSession session = sessionOf(user.userId());
		StpUtil.login(user.userId());
		SaSession tokenSession = StpUtil.getTokenSession();
		tokenSession.set("userId", session.userId());
		tokenSession.set("username", session.username());
		tokenSession.set("roles", session.roles());
		tokenSession.set("permissions", session.permissions());
		return LoginResponse.of(session, StpUtil.getTokenValue());
	}

	@Override
	public AuthSession session() {
		StpUtil.checkLogin();
		SaSession tokenSession = StpUtil.getTokenSession();
		Object userId = tokenSession.get("userId");
		Object username = tokenSession.get("username");
		if (userId != null && username != null) {
			return new AuthSession(
					Long.valueOf(String.valueOf(userId)),
					String.valueOf(username),
					asStringList(tokenSession.get("roles")),
					asStringList(tokenSession.get("permissions"))
			);
		}
		return sessionOf(StpUtil.getLoginIdAsLong());
	}

	@Override
	public void logout() {
		StpUtil.logout();
	}

	private AuthSession sessionOf(Long userId) {
		DemoUser user = rbacStore.getById(userId);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found");
		}
		return new AuthSession(user.userId(), user.username(), rbacStore.getRoles(user.userId()), rbacStore.getPermissions(user.userId()));
	}

	private List<String> asStringList(Object value) {
		if (value instanceof List<?> list) {
			return list.stream().map(String::valueOf).toList();
		}
		return List.of();
	}

}

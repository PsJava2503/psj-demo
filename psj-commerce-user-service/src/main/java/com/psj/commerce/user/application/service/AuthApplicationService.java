package com.psj.commerce.user.application.service;

import cn.dev33.satoken.stp.StpUtil;
import com.psj.commerce.security.AuthSession;
import com.psj.commerce.user.application.port.AuthUseCase;
import com.psj.commerce.user.infrastructure.persistence.DemoRbacStore;
import com.psj.commerce.user.infrastructure.persistence.DemoRbacStore.DemoUser;
import com.psj.commerce.user.interfaces.rest.LoginRequest;
import com.psj.commerce.user.interfaces.rest.LoginResponse;
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
		StpUtil.login(user.userId());
		return LoginResponse.of(sessionOf(user.userId()), StpUtil.getTokenValue());
	}

	@Override
	public AuthSession session() {
		StpUtil.checkLogin();
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

}

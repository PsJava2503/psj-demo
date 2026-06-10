package com.commerce.user.application.port;

import com.commerce.security.AuthSession;
import com.commerce.user.interfaces.rest.LoginRequest;
import com.commerce.user.interfaces.rest.LoginResponse;

public interface AuthUseCase {

	LoginResponse login(LoginRequest request);

	AuthSession session();

	void logout();

}

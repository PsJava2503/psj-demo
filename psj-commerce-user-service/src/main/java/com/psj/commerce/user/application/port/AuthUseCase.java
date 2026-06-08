package com.psj.commerce.user.application.port;

import com.psj.commerce.security.AuthSession;
import com.psj.commerce.user.interfaces.rest.LoginRequest;
import com.psj.commerce.user.interfaces.rest.LoginResponse;

public interface AuthUseCase {

	LoginResponse login(LoginRequest request);

	AuthSession session();

	void logout();

}

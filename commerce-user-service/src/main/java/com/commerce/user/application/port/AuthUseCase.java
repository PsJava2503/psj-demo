package com.commerce.user.application.port;

import com.commerce.security.AuthSession;
import com.commerce.user.interfaces.rest.ChangePasswordRequest;
import com.commerce.user.interfaces.rest.DisableLoginRequest;
import com.commerce.user.interfaces.rest.LoginRequest;
import com.commerce.user.interfaces.rest.LoginResponse;
import com.commerce.user.interfaces.rest.RegisterRequest;

public interface AuthUseCase {

	LoginResponse register(RegisterRequest request);

	LoginResponse login(LoginRequest request);

	AuthSession session();

	void changePassword(ChangePasswordRequest request);

	void disableLogin(DisableLoginRequest request);

	void logout();

}

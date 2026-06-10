package com.commerce.user.interfaces.rest;

import com.commerce.security.AuthSession;
import com.commerce.user.application.port.AuthUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthUseCase authUseCase;

	public AuthController(AuthUseCase authUseCase) {
		this.authUseCase = authUseCase;
	}

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request) {
		return authUseCase.login(request);
	}

	@GetMapping("/session")
	public AuthSession session() {
		return authUseCase.session();
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout() {
		authUseCase.logout();
	}

}

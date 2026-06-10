package com.psj.commerce.user.interfaces.rest;

import com.psj.commerce.user.application.port.UserUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

	private final UserUseCase userUseCase;

	public InternalUserController(UserUseCase userUseCase) {
		this.userUseCase = userUseCase;
	}

	@GetMapping("/{userId}")
	public String getUserName(@PathVariable Long userId) {
		return userUseCase.getUserName(userId);
	}
}

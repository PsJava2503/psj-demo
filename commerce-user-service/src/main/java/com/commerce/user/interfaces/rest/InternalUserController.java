package com.commerce.user.interfaces.rest;

import com.commerce.user.UserResponse;
import com.commerce.user.application.port.UserUseCase;
import com.commerce.user.domain.model.UserQueryOptions;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

	private final UserUseCase userUseCase;

	public InternalUserController(UserUseCase userUseCase) {
		this.userUseCase = userUseCase;
	}

	@GetMapping("/{userId}")
	public UserResponse get(@PathVariable Long userId) {
		UserQueryOptions options = new UserQueryOptions(
				Optional.of(userId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(false),
				Optional.empty()
		);
		return userUseCase.query(options).stream()
				.findFirst()
				.map(UserRepresentationMapper::toResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
	}
}

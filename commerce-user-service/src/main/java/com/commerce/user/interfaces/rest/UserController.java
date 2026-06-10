package com.commerce.user.interfaces.rest;

import com.commerce.user.UserResponse;
import com.commerce.user.application.port.UserUseCase;
import com.commerce.user.domain.model.UserQueryOptions;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserUseCase userUseCase;

	public UserController(UserUseCase userUseCase) {
		this.userUseCase = userUseCase;
	}

	@GetMapping
	public List<UserResponse> query(
			@RequestParam Optional<Long> id,
			@RequestParam Optional<String> firstName,
			@RequestParam Optional<String> secondName,
			@RequestParam Optional<String> phone,
			@RequestParam Optional<String> email,
			@RequestParam Optional<Long> defaultAddressId,
			@RequestParam Optional<Boolean> deleted,
			@RequestParam Optional<ZonedDateTime> createTime
	) {
		UserQueryOptions options = new UserQueryOptions(
				id,
				firstName,
				secondName,
				phone,
				email,
				defaultAddressId,
				deleted,
				createTime
		);
		return userUseCase.query(options).stream()
				.map(UserRepresentationMapper::toResponse)
				.toList();
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

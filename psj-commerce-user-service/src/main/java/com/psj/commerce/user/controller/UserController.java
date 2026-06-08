package com.psj.commerce.user.controller;

import com.psj.commerce.api.UserRpcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserRpcService userRpcService;

	public UserController(UserRpcService userRpcService) {
		this.userRpcService = userRpcService;
	}

	@GetMapping("/{userId}")
	public String get(@PathVariable Long userId) {
		return userRpcService.getUserName(userId);
	}

}

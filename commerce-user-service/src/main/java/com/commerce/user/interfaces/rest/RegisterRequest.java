package com.commerce.user.interfaces.rest;

public record RegisterRequest(
		String firstName,
		String secondName,
		String phone,
		String email,
		String username,
		String password
) {
}

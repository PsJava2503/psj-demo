package com.commerce.user.domain.model;

import java.util.Optional;

public class UserCredentialQueryOptions {

	private final Optional<Long> userId;
	private final Optional<String> username;
	private final Optional<Boolean> enabled;

	public UserCredentialQueryOptions(Optional<Long> userId, Optional<String> username, Optional<Boolean> enabled) {
		this.userId = userId == null ? Optional.empty() : userId;
		this.username = username == null ? Optional.empty() : username;
		this.enabled = enabled == null ? Optional.empty() : enabled;
	}

	public static UserCredentialQueryOptions none() {
		return new UserCredentialQueryOptions(Optional.empty(), Optional.empty(), Optional.empty());
	}

	public Optional<Long> getUserId() {
		return userId;
	}

	public Optional<String> getUsername() {
		return username;
	}

	public Optional<Boolean> getEnabled() {
		return enabled;
	}

}

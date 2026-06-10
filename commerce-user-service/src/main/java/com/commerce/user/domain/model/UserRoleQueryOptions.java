package com.commerce.user.domain.model;

import java.util.Optional;

public class UserRoleQueryOptions {

	private final Optional<Long> userId;
	private final Optional<Long> roleId;

	public UserRoleQueryOptions(Optional<Long> userId, Optional<Long> roleId) {
		this.userId = userId == null ? Optional.empty() : userId;
		this.roleId = roleId == null ? Optional.empty() : roleId;
	}

	public static UserRoleQueryOptions none() {
		return new UserRoleQueryOptions(Optional.empty(), Optional.empty());
	}

	public Optional<Long> getUserId() {
		return userId;
	}

	public Optional<Long> getRoleId() {
		return roleId;
	}

}

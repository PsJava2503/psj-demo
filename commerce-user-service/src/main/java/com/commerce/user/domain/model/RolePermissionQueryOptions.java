package com.commerce.user.domain.model;

import java.util.Optional;

public class RolePermissionQueryOptions {

	private final Optional<Long> roleId;
	private final Optional<Long> permissionId;

	public RolePermissionQueryOptions(Optional<Long> roleId, Optional<Long> permissionId) {
		this.roleId = roleId == null ? Optional.empty() : roleId;
		this.permissionId = permissionId == null ? Optional.empty() : permissionId;
	}

	public static RolePermissionQueryOptions none() {
		return new RolePermissionQueryOptions(Optional.empty(), Optional.empty());
	}

	public Optional<Long> getRoleId() {
		return roleId;
	}

	public Optional<Long> getPermissionId() {
		return permissionId;
	}

}

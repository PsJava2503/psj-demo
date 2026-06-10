package com.commerce.user.domain.model;

import java.util.Optional;

public class RoleQueryOptions {

	private final Optional<Long> id;
	private final Optional<String> code;
	private final Optional<Boolean> enabled;
	private final Optional<Boolean> deleted;

	public RoleQueryOptions(Optional<Long> id, Optional<String> code, Optional<Boolean> enabled, Optional<Boolean> deleted) {
		this.id = id == null ? Optional.empty() : id;
		this.code = code == null ? Optional.empty() : code;
		this.enabled = enabled == null ? Optional.empty() : enabled;
		this.deleted = deleted == null ? Optional.empty() : deleted;
	}

	public static RoleQueryOptions none() {
		return new RoleQueryOptions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	public Optional<Long> getId() {
		return id;
	}

	public Optional<String> getCode() {
		return code;
	}

	public Optional<Boolean> getEnabled() {
		return enabled;
	}

	public Optional<Boolean> getDeleted() {
		return deleted;
	}

}

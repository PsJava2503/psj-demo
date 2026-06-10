package com.commerce.user.domain.model;

import java.util.Optional;

public class PermissionQueryOptions {

	private final Optional<Long> id;
	private final Optional<String> code;
	private final Optional<String> resource;
	private final Optional<String> action;
	private final Optional<Boolean> enabled;
	private final Optional<Boolean> deleted;

	public PermissionQueryOptions(
			Optional<Long> id,
			Optional<String> code,
			Optional<String> resource,
			Optional<String> action,
			Optional<Boolean> enabled,
			Optional<Boolean> deleted
	) {
		this.id = id == null ? Optional.empty() : id;
		this.code = code == null ? Optional.empty() : code;
		this.resource = resource == null ? Optional.empty() : resource;
		this.action = action == null ? Optional.empty() : action;
		this.enabled = enabled == null ? Optional.empty() : enabled;
		this.deleted = deleted == null ? Optional.empty() : deleted;
	}

	public static PermissionQueryOptions none() {
		return new PermissionQueryOptions(
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		);
	}

	public Optional<Long> getId() {
		return id;
	}

	public Optional<String> getCode() {
		return code;
	}

	public Optional<String> getResource() {
		return resource;
	}

	public Optional<String> getAction() {
		return action;
	}

	public Optional<Boolean> getEnabled() {
		return enabled;
	}

	public Optional<Boolean> getDeleted() {
		return deleted;
	}

}

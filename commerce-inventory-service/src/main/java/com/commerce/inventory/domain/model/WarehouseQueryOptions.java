package com.commerce.inventory.domain.model;

import java.util.Optional;

public class WarehouseQueryOptions {

	private final Optional<Long> id;

	public WarehouseQueryOptions(Optional<Long> id) {
		this.id = id == null ? Optional.empty() : id;
	}

	public static WarehouseQueryOptions none() {
		return new WarehouseQueryOptions(Optional.empty());
	}

	public static WarehouseQueryOptions byId(Long id) {
		return new WarehouseQueryOptions(Optional.ofNullable(id));
	}

	public Long getIdValue() {
		return id.orElse(null);
	}
}

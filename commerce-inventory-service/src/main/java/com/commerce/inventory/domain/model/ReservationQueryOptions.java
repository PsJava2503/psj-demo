package com.commerce.inventory.domain.model;

import java.util.Optional;

public class ReservationQueryOptions {

	private final Optional<Long> id;

	public ReservationQueryOptions(Optional<Long> id) {
		this.id = id == null ? Optional.empty() : id;
	}

	public static ReservationQueryOptions none() {
		return new ReservationQueryOptions(Optional.empty());
	}

	public static ReservationQueryOptions byId(Long id) {
		return new ReservationQueryOptions(Optional.ofNullable(id));
	}

	public Long getIdValue() {
		return id.orElse(null);
	}
}

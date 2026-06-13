package com.commerce.address.domain.model;

import java.util.Optional;

public class AddressQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> userId;
	private final Optional<String> phone;
	private final Optional<Boolean> defaultAddress;
	private final Optional<Boolean> deleted;

	public AddressQueryOptions(
			Optional<Long> id,
			Optional<Long> userId,
			Optional<String> phone,
			Optional<Boolean> defaultAddress,
			Optional<Boolean> deleted
	) {
		this.id = id == null ? Optional.empty() : id;
		this.userId = userId == null ? Optional.empty() : userId;
		this.phone = phone == null ? Optional.empty() : phone;
		this.defaultAddress = defaultAddress == null ? Optional.empty() : defaultAddress;
		this.deleted = deleted == null ? Optional.empty() : deleted;
	}

	public static AddressQueryOptions none() {
		return new AddressQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	public Optional<Long> getId() {
		return id;
	}

	public Optional<Long> getUserId() {
		return userId;
	}

	public Optional<String> getPhone() {
		return phone;
	}

	public Optional<Boolean> getDefaultAddress() {
		return defaultAddress;
	}

	public Optional<Boolean> getDeleted() {
		return deleted;
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public Long getUserIdValue() {
		return userId.orElse(null);
	}

	public String getPhoneValue() {
		return phone.orElse(null);
	}

	public Boolean getDefaultAddressValue() {
		return defaultAddress.orElse(null);
	}

	public Boolean getDeletedValue() {
		return deleted.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent()
				|| userId.isPresent()
				|| phone.isPresent()
				|| defaultAddress.isPresent()
				|| deleted.isPresent();
	}
}

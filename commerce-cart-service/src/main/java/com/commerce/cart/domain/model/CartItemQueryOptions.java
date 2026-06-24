package com.commerce.cart.domain.model;

import java.util.Optional;

public class CartItemQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> userId;
	private final Optional<Long> productId;
	private final Optional<Boolean> selected;
	private final Optional<Boolean> deleted;

	public CartItemQueryOptions(
			Optional<Long> id,
			Optional<Long> userId,
			Optional<Long> productId,
			Optional<Boolean> selected,
			Optional<Boolean> deleted
	) {
		this.id = id == null ? Optional.empty() : id;
		this.userId = userId == null ? Optional.empty() : userId;
		this.productId = productId == null ? Optional.empty() : productId;
		this.selected = selected == null ? Optional.empty() : selected;
		this.deleted = deleted == null ? Optional.empty() : deleted;
	}

	public static CartItemQueryOptions none() {
		return new CartItemQueryOptions(
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

	public Optional<Long> getProductId() {
		return productId;
	}

	public Optional<Boolean> getSelected() {
		return selected;
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

	public Long getProductIdValue() {
		return productId.orElse(null);
	}

	public Boolean getSelectedValue() {
		return selected.orElse(null);
	}

	public Boolean getDeletedValue() {
		return deleted.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent()
				|| userId.isPresent()
				|| productId.isPresent()
				|| selected.isPresent()
				|| deleted.isPresent();
	}
}

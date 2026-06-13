package com.commerce.product.domain.model;

import java.util.Optional;

public class ProductQueryOptions {

	private final Optional<Long> id;
	private final Optional<String> name;
	private final Optional<Long> skuId;
	private final Optional<Boolean> enabled;
	private final Optional<Boolean> deleted;

	public ProductQueryOptions(
			Optional<Long> id,
			Optional<String> name,
			Optional<Long> skuId,
			Optional<Boolean> enabled,
			Optional<Boolean> deleted
	) {
		this.id = id == null ? Optional.empty() : id;
		this.name = name == null ? Optional.empty() : name;
		this.skuId = skuId == null ? Optional.empty() : skuId;
		this.enabled = enabled == null ? Optional.empty() : enabled;
		this.deleted = deleted == null ? Optional.empty() : deleted;
	}

	public static ProductQueryOptions none() {
		return new ProductQueryOptions(
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

	public Optional<String> getName() {
		return name;
	}

	public Optional<Long> getSkuId() {
		return skuId;
	}

	public Optional<Boolean> getEnabled() {
		return enabled;
	}

	public Optional<Boolean> getDeleted() {
		return deleted;
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public String getNameValue() {
		return name.orElse(null);
	}

	public Long getSkuIdValue() {
		return skuId.orElse(null);
	}

	public Boolean getEnabledValue() {
		return enabled.orElse(null);
	}

	public Boolean getDeletedValue() {
		return deleted.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent()
				|| name.isPresent()
				|| skuId.isPresent()
				|| enabled.isPresent()
				|| deleted.isPresent();
	}
}

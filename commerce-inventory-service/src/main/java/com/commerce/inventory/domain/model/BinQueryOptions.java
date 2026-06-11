package com.commerce.inventory.domain.model;

import java.util.List;
import java.util.Optional;

public class BinQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> warehouseId;
	private final Optional<List<Long>> ids;

	public BinQueryOptions(Optional<Long> id, Optional<Long> warehouseId, Optional<List<Long>> ids) {
		this.id = id == null ? Optional.empty() : id;
		this.warehouseId = warehouseId == null ? Optional.empty() : warehouseId;
		this.ids = ids == null ? Optional.empty() : ids;
	}

	public static BinQueryOptions none() {
		return new BinQueryOptions(Optional.empty(), Optional.empty(), Optional.empty());
	}

	public static BinQueryOptions byId(Long id) {
		return new BinQueryOptions(Optional.ofNullable(id), Optional.empty(), Optional.empty());
	}

	public static BinQueryOptions byWarehouseId(Long warehouseId) {
		return new BinQueryOptions(Optional.empty(), Optional.ofNullable(warehouseId), Optional.empty());
	}

	public static BinQueryOptions byIds(List<Long> ids) {
		return new BinQueryOptions(Optional.empty(), Optional.empty(), Optional.ofNullable(ids));
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public Long getWarehouseIdValue() {
		return warehouseId.orElse(null);
	}

	public List<Long> getIdsValue() {
		return ids.orElse(null);
	}

	public boolean hasIds() {
		return ids.isPresent() && !ids.get().isEmpty();
	}
}

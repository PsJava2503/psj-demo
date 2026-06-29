package com.commerce.order.application.command;

import java.util.List;

public record CreateOrderCommand(Long userId, Long productId, Long addressId, Integer quantity, List<Item> items) {

	public CreateOrderCommand(Long userId, Long productId, Long addressId, Integer quantity) {
		this(userId, productId, addressId, quantity, List.of(new Item(productId, quantity)));
	}

	public List<Item> items() {
		if (items == null || items.isEmpty()) {
			return List.of(new Item(productId, quantity));
		}
		return items;
	}

	public record Item(Long productId, Integer quantity) {
	}
}

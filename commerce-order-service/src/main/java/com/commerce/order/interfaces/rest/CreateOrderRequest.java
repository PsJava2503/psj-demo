package com.commerce.order.interfaces.rest;

import java.util.List;

public record CreateOrderRequest(Long userId, Long productId, Long addressId, Integer quantity, List<ItemRequest> items) {

	public record ItemRequest(Long productId, Integer quantity) {
	}
}

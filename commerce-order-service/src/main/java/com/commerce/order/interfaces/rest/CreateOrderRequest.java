package com.commerce.order.interfaces.rest;

public record CreateOrderRequest(Long userId, Long productId, Long addressId, Integer quantity) {
}

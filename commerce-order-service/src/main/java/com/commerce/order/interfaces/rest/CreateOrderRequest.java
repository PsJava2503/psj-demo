package com.commerce.order.interfaces.rest;

public record CreateOrderRequest(Long userId, Long productId, Integer quantity) {
}

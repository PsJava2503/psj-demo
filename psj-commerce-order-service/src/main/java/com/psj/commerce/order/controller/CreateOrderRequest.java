package com.psj.commerce.order.controller;

public record CreateOrderRequest(Long userId, Long productId, Integer quantity) {
}

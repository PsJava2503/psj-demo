package com.commerce.order.application.command;

public record CreateOrderCommand(Long userId, Long productId, Long addressId, Integer quantity) {
}

package com.psj.commerce.order.application.command;

public record CreateOrderCommand(Long userId, Long productId, Integer quantity) {
}

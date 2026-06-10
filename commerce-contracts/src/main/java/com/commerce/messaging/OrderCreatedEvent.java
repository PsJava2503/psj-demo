package com.commerce.messaging;

import java.math.BigDecimal;

public record OrderCreatedEvent(Long orderId, Long userId, Long productId, Integer quantity, BigDecimal amount) {
}

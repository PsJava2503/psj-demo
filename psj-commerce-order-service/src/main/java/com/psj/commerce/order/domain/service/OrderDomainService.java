package com.psj.commerce.order.domain.service;

import com.psj.commerce.order.domain.model.Order;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class OrderDomainService {

	public Order create(Long userId, Long productId, Integer quantity, BigDecimal price) {
		if (userId == null || productId == null || quantity == null || quantity <= 0) {
			throw new IllegalArgumentException("invalid order request");
		}
		BigDecimal amount = price.multiply(BigDecimal.valueOf(quantity));
		return new Order(Instant.now().toEpochMilli(), userId, productId, quantity, amount);
	}

}

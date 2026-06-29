package com.commerce.order.domain.service;

import com.commerce.address.AddressResponse;
import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderStatus;
import com.commerce.product.ProductResponse;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Service;

@Service
public class OrderDomainService {

	private final OrderIdGenerator orderIdGenerator;

	public OrderDomainService(OrderIdGenerator orderIdGenerator) {
		this.orderIdGenerator = orderIdGenerator;
	}

	public Order create(Long userId, ProductResponse product, AddressResponse address, Integer quantity) {
		Long productId = product == null ? null : product.id();
		if (userId == null || productId == null || quantity == null || quantity <= 0) {
			throw new IllegalArgumentException("invalid order request");
		}
		if (product.price() == null || product.price().compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("invalid product price");
		}
		BigDecimal amount = product.price().multiply(BigDecimal.valueOf(quantity));
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		return new Order(
				orderIdGenerator.nextId(),
				orderIdGenerator.nextOrderNo(),
				userId,
				product.id(),
				product.skuId(),
				product.name(),
				product.price(),
				quantity,
				amount,
				address == null ? null : address.id(),
				address == null ? null : address.recipientName(),
				address == null ? null : address.phone(),
				address == null ? null : address.province(),
				address == null ? null : address.city(),
				address == null ? null : address.district(),
				address == null ? null : address.detail(),
				0L,
				OrderStatus.CREATED,
				now,
				now
		);
	}

	public Order createCheckout(Long userId, ProductResponse primaryProduct, AddressResponse address, Integer totalQuantity, BigDecimal totalAmount) {
		if (userId == null || primaryProduct == null || primaryProduct.id() == null || totalQuantity == null || totalQuantity <= 0) {
			throw new IllegalArgumentException("invalid order request");
		}
		if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("invalid order amount");
		}
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		return new Order(
				orderIdGenerator.nextId(),
				orderIdGenerator.nextOrderNo(),
				userId,
				primaryProduct.id(),
				primaryProduct.skuId(),
				primaryProduct.name(),
				primaryProduct.price(),
				totalQuantity,
				totalAmount,
				address == null ? null : address.id(),
				address == null ? null : address.recipientName(),
				address == null ? null : address.phone(),
				address == null ? null : address.province(),
				address == null ? null : address.city(),
				address == null ? null : address.district(),
				address == null ? null : address.detail(),
				0L,
				OrderStatus.CREATED,
				now,
				now
		);
	}

}

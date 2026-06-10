package com.commerce.order.domain.model;

import java.math.BigDecimal;

public class Order {

	private final Long orderId;
	private final Long userId;
	private final Long productId;
	private final Integer quantity;
	private final BigDecimal amount;
	private OrderStatus status;

	public Order(Long orderId, Long userId, Long productId, Integer quantity, BigDecimal amount) {
		this.orderId = orderId;
		this.userId = userId;
		this.productId = productId;
		this.quantity = quantity;
		this.amount = amount;
		this.status = OrderStatus.CREATED;
	}

	public void markStockDeducted() {
		this.status = OrderStatus.STOCK_DEDUCTED;
	}

	public void markPaid() {
		this.status = OrderStatus.PAID;
	}

	public void complete() {
		this.status = OrderStatus.COMPLETED;
	}

	public void fail() {
		this.status = OrderStatus.FAILED;
	}

	public Long orderId() {
		return orderId;
	}

	public Long userId() {
		return userId;
	}

	public Long productId() {
		return productId;
	}

	public Integer quantity() {
		return quantity;
	}

	public BigDecimal amount() {
		return amount;
	}

	public OrderStatus status() {
		return status;
	}

}

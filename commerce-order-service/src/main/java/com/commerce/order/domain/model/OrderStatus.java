package com.commerce.order.domain.model;

public enum OrderStatus {
	CREATED,
	STOCK_DEDUCTED,
	WAIT_PAY,
	PAID,
	COMPLETED,
	FAILED
}

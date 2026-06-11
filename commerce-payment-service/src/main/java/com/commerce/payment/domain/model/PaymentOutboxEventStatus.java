package com.commerce.payment.domain.model;

public enum PaymentOutboxEventStatus {
	NEW,
	PUBLISHING,
	PUBLISHED,
	FAILED
}

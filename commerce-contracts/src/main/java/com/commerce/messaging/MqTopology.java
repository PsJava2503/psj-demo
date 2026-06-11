package com.commerce.messaging;

public final class MqTopology {

	public static final String ORDER_EXCHANGE = "commerce.order.exchange";
	public static final String ORDER_CREATED_QUEUE = "commerce.order.created.queue";
	public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
	public static final String PAYMENT_EXCHANGE = "commerce.payment.exchange";
	public static final String PAYMENT_PAID_QUEUE = "commerce.payment.paid.queue";
	public static final String PAYMENT_PAID_ROUTING_KEY = "payment.paid";

	private MqTopology() {
	}
}

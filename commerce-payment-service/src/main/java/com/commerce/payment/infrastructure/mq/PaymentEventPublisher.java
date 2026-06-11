package com.commerce.payment.infrastructure.mq;

import com.commerce.messaging.MqTopology;
import com.commerce.messaging.PaymentPaidEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

	private final RabbitTemplate rabbitTemplate;

	public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publishPaymentPaid(PaymentPaidEvent event) {
		rabbitTemplate.convertAndSend(MqTopology.PAYMENT_EXCHANGE, MqTopology.PAYMENT_PAID_ROUTING_KEY, event);
	}
}

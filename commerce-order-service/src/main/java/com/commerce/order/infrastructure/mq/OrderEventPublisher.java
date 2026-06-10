package com.commerce.order.infrastructure.mq;

import com.commerce.messaging.MqTopology;
import com.commerce.messaging.OrderCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

	private final RabbitTemplate rabbitTemplate;

	public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publishOrderCreated(OrderCreatedEvent event) {
		rabbitTemplate.convertAndSend(MqTopology.ORDER_EXCHANGE, MqTopology.ORDER_CREATED_ROUTING_KEY, event);
	}
}

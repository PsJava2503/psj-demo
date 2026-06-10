package com.commerce.notification.infrastructure.mq;

import com.commerce.messaging.MqTopology;
import com.commerce.messaging.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

	private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

	@RabbitListener(queues = MqTopology.ORDER_CREATED_QUEUE)
	public void onOrderCreated(OrderCreatedEvent event) {
		// MQ integration scaffold only. Business handling will be implemented later.
		log.info("Received order-created event: {}", event);
	}
}

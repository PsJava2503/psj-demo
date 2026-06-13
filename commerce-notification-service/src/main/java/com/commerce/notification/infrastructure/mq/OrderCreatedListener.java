package com.commerce.notification.infrastructure.mq;

import com.commerce.messaging.MqTopology;
import com.commerce.messaging.OrderCreatedEvent;
import com.commerce.notification.domain.model.NotificationRecordQueryOptions;
import com.commerce.notification.infrastructure.persistence.NotificationRecordRepository;
import com.commerce.notification.infrastructure.persistence.model.NotificationRecordData;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

	private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

	private final NotificationRecordRepository notificationRecordRepository;

	public OrderCreatedListener(NotificationRecordRepository notificationRecordRepository) {
		this.notificationRecordRepository = notificationRecordRepository;
	}

	@RabbitListener(queues = MqTopology.ORDER_CREATED_QUEUE)
	public void onOrderCreated(OrderCreatedEvent event) {
		String idempotencyKey = "order-wait-pay:" + event.orderId();
		NotificationRecordQueryOptions options = new NotificationRecordQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(idempotencyKey)
		);
		if (!notificationRecordRepository.query(options).isEmpty()) {
			return;
		}
		notificationRecordRepository.create(new NotificationRecordData(
				null,
				event.orderId(),
				event.userId(),
				"ORDER_WAIT_PAY",
				"LOG",
				null,
				event.toString(),
				"SENT",
				idempotencyKey,
				ZonedDateTime.now(),
				"",
				null,
				null
		));
		log.info("Received order-created event: {}", event);
	}
}

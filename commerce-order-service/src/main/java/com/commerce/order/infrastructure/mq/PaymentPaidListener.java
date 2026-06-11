package com.commerce.order.infrastructure.mq;

import com.commerce.messaging.MqTopology;
import com.commerce.messaging.PaymentPaidEvent;
import com.commerce.order.application.port.NotificationCommandPort;
import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderQueryOptions;
import com.commerce.order.domain.model.OrderStatus;
import com.commerce.order.domain.repository.OrderRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentPaidListener {

	private static final Logger log = LoggerFactory.getLogger(PaymentPaidListener.class);

	private final OrderRepository orderRepository;
	private final NotificationCommandPort notificationCommandPort;

	public PaymentPaidListener(OrderRepository orderRepository, NotificationCommandPort notificationCommandPort) {
		this.orderRepository = orderRepository;
		this.notificationCommandPort = notificationCommandPort;
	}

	@RabbitListener(queues = MqTopology.PAYMENT_PAID_QUEUE)
	public void onPaymentPaid(PaymentPaidEvent event) {
		Optional<Order> order = orderRepository.query(new OrderQueryOptions(
				Optional.ofNullable(event.orderId()),
				Optional.empty()
		)).stream().findFirst();
		if (order.isEmpty()) {
			log.warn("Received payment-paid event for missing order: {}", event);
			return;
		}
		Order paidOrder = order.get();
		if (paidOrder.status() == OrderStatus.COMPLETED || paidOrder.status() == OrderStatus.PAID) {
			log.info("Ignoring duplicated payment-paid event: {}", event);
			return;
		}
		if (paidOrder.status() != OrderStatus.WAIT_PAY) {
			log.warn("Received payment-paid event for order in unexpected status: orderId={}, status={}", paidOrder.orderId(), paidOrder.status());
			return;
		}

		paidOrder.markPaid();
		notificationCommandPort.notifyOrderPaid(paidOrder.orderId());
		paidOrder.complete();
		orderRepository.save(paidOrder);
		log.info("Order completed after payment paid: {}", event);
	}
}

package com.commerce.order.infrastructure.mq;

import com.commerce.messaging.MqTopology;
import com.commerce.messaging.PaymentPaidEvent;
import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderInventoryReservation;
import com.commerce.order.domain.model.OrderInventoryReservationQueryOptions;
import com.commerce.order.domain.model.OrderOutboxEventQueryOptions;
import com.commerce.order.domain.model.OrderQueryOptions;
import com.commerce.order.domain.model.OrderStatus;
import com.commerce.order.domain.model.OrderStatusLog;
import com.commerce.order.domain.repository.OrderRepository;
import com.commerce.order.infrastructure.persistence.OrderInventoryReservationRepository;
import com.commerce.order.infrastructure.persistence.OrderOutboxEventRepository;
import com.commerce.order.infrastructure.persistence.OrderStatusLogRepository;
import com.commerce.order.infrastructure.persistence.model.OrderOutboxEventData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentPaidListener {

	private static final Logger log = LoggerFactory.getLogger(PaymentPaidListener.class);

	private final OrderRepository orderRepository;
	private final OrderInventoryReservationRepository reservationRepository;
	private final OrderOutboxEventRepository orderOutboxEventRepository;
	private final OrderStatusLogRepository orderStatusLogRepository;
	private final ObjectMapper objectMapper;

	public PaymentPaidListener(
			OrderRepository orderRepository,
			OrderInventoryReservationRepository reservationRepository,
			OrderOutboxEventRepository orderOutboxEventRepository,
			OrderStatusLogRepository orderStatusLogRepository,
			ObjectMapper objectMapper
	) {
		this.orderRepository = orderRepository;
		this.reservationRepository = reservationRepository;
		this.orderOutboxEventRepository = orderOutboxEventRepository;
		this.orderStatusLogRepository = orderStatusLogRepository;
		this.objectMapper = objectMapper;
	}

	@RabbitListener(queues = MqTopology.PAYMENT_PAID_QUEUE)
	public void onPaymentPaid(PaymentPaidEvent event) {
		Optional<Order> order = orderRepository.query(new OrderQueryOptions(
				Optional.ofNullable(event.orderId()),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		)).stream().findFirst();
		if (order.isEmpty()) {
			log.warn("Received payment-paid event for missing order: {}", event);
			return;
		}
		Order paidOrder = order.get();
		if (paidOrder.status() == OrderStatus.CANCELLED || paidOrder.status() == OrderStatus.FAILED) {
			OrderStatus fromStatus = paidOrder.status();
			paidOrder.requireRefund();
			int updated = orderRepository.update(paidOrder, new OrderQueryOptions(
					Optional.ofNullable(paidOrder.orderId()),
					Optional.empty(),
					Optional.of(fromStatus),
					Optional.empty()
			));
			if (updated == 1) {
				logStatus(paidOrder.orderId(), fromStatus, paidOrder.status(), "paid after order closed");
				createOutbox("refund-requested:" + paidOrder.orderId(), "REFUND_REQUESTED", paidOrder.orderId(),
						Map.of("orderId", paidOrder.orderId(), "amount", paidOrder.amount(), "reason", "paid_after_order_closed"));
			}
			return;
		}
		if (paidOrder.status() == OrderStatus.COMPLETED || paidOrder.status() == OrderStatus.PAID) {
			log.info("Ignoring duplicated payment-paid event: {}", event);
			return;
		}
		if (paidOrder.status() != OrderStatus.WAIT_PAY) {
			log.warn("Received payment-paid event for order in unexpected status: orderId={}, status={}", paidOrder.orderId(), paidOrder.status());
			return;
		}

		OrderStatus fromStatus = paidOrder.status();
		paidOrder.markPaid();
		List<Long> reservationIds = reservationIdsOf(paidOrder.orderId());
		int updated = orderRepository.update(paidOrder, new OrderQueryOptions(
				Optional.ofNullable(paidOrder.orderId()),
				Optional.empty(),
				Optional.of(fromStatus),
				Optional.empty()
		));
		if (updated != 1) {
			log.info("Ignoring concurrently updated payment-paid event: {}", event);
			return;
		}
		logStatus(paidOrder.orderId(), fromStatus, paidOrder.status(), "payment paid");
		createOutbox("order-paid:" + paidOrder.orderId(), "ORDER_PAID", paidOrder.orderId(),
				Map.of("orderId", paidOrder.orderId()));
		createOutbox("inventory-confirm:" + paidOrder.orderId(), "INVENTORY_CONFIRM_REQUESTED", paidOrder.orderId(),
				Map.of("orderId", paidOrder.orderId(), "reservationIds", reservationIds));
		createOutbox("notification-order-paid:" + paidOrder.orderId(), "NOTIFICATION_REQUESTED", paidOrder.orderId(),
				Map.of("orderId", paidOrder.orderId(), "templateCode", "ORDER_PAID"));
		log.info("Order marked paid after payment event: {}", event);
	}

	private List<Long> reservationIdsOf(Long orderId) {
		return reservationRepository.query(new OrderInventoryReservationQueryOptions(
				Optional.empty(),
				Optional.ofNullable(orderId),
				Optional.empty(),
				Optional.of("RESERVED")
		)).stream()
				.map(OrderInventoryReservation::reservationId)
				.toList();
	}

	private void createOutbox(String eventKey, String eventType, Long aggregateId, Map<String, Object> payload) {
		if (!orderOutboxEventRepository.query(new OrderOutboxEventQueryOptions(
				Optional.empty(),
				Optional.of(eventKey),
				Optional.empty(),
				Optional.empty()
		)).isEmpty()) {
			return;
		}
		try {
			orderOutboxEventRepository.create(new OrderOutboxEventData(
					null,
					eventKey,
					eventType,
					aggregateId,
					objectMapper.writeValueAsString(payload),
					"NEW",
					0,
					"",
					null,
					null,
					null
			));
		} catch (JsonProcessingException ex) {
			throw new IllegalStateException("failed to serialize order outbox event", ex);
		}
	}

	private void logStatus(Long orderId, OrderStatus fromStatus, OrderStatus toStatus, String reason) {
		orderStatusLogRepository.create(new OrderStatusLog(null, orderId, fromStatus, toStatus, reason, null));
	}
}

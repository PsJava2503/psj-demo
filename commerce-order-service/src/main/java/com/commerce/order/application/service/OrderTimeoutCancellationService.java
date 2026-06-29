package com.commerce.order.application.service;

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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderTimeoutCancellationService {

	private final OrderRepository orderRepository;
	private final OrderInventoryReservationRepository reservationRepository;
	private final OrderStatusLogRepository orderStatusLogRepository;
	private final OrderOutboxEventRepository orderOutboxEventRepository;
	private final ObjectMapper objectMapper;
	private final int timeoutMinutes;

	public OrderTimeoutCancellationService(
			OrderRepository orderRepository,
			OrderInventoryReservationRepository reservationRepository,
			OrderStatusLogRepository orderStatusLogRepository,
			OrderOutboxEventRepository orderOutboxEventRepository,
			ObjectMapper objectMapper,
			@Value("${order.payment-timeout-minutes:30}") int timeoutMinutes
	) {
		this.orderRepository = orderRepository;
		this.reservationRepository = reservationRepository;
		this.orderStatusLogRepository = orderStatusLogRepository;
		this.orderOutboxEventRepository = orderOutboxEventRepository;
		this.objectMapper = objectMapper;
		this.timeoutMinutes = timeoutMinutes;
	}

	@Scheduled(fixedDelayString = "${order.timeout-scan-delay-ms:60000}")
	@Transactional
	public void cancelTimedOutOrders() {
		ZonedDateTime deadline = ZonedDateTime.now().minusMinutes(timeoutMinutes);
		List<Order> orders = orderRepository.query(new OrderQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.of(OrderStatus.WAIT_PAY),
				Optional.of(deadline)
		));
		for (Order order : orders) {
			cancel(order);
		}
	}

	private void cancel(Order order) {
		List<Long> reservationIds = reservationRepository.query(new OrderInventoryReservationQueryOptions(
				Optional.empty(),
				Optional.ofNullable(order.orderId()),
				Optional.empty(),
				Optional.of("RESERVED")
		)).stream()
				.map(OrderInventoryReservation::reservationId)
				.toList();
		OrderStatus fromStatus = order.status();
		order.cancel();
		int updated = orderRepository.update(order, new OrderQueryOptions(
				Optional.ofNullable(order.orderId()),
				Optional.empty(),
				Optional.of(fromStatus),
				Optional.empty()
		));
		if (updated != 1) {
			return;
		}
		orderStatusLogRepository.create(new OrderStatusLog(null, order.orderId(), fromStatus, order.status(), "payment timeout", null));
		createGenericOutbox("order-cancelled:" + order.orderId(), "ORDER_CANCELLED", order.orderId(),
				Map.of("orderId", order.orderId(), "userId", order.userId(), "reason", "payment timeout"));
		createGenericOutbox("notification-order-cancelled:" + order.orderId(), "NOTIFICATION_REQUESTED", order.orderId(),
				Map.of("orderId", order.orderId(), "templateCode", "ORDER_CANCELLED"));
		createOutbox("inventory-release-timeout:" + order.orderId(), order.orderId(), reservationIds);
		createGenericOutbox("payment-close-timeout:" + order.orderId(), "PAYMENT_CLOSE_REQUESTED", order.orderId(),
				Map.of("orderId", order.orderId(), "reason", "payment timeout"));
	}

	private void createOutbox(String eventKey, Long orderId, List<Long> reservationIds) {
		createGenericOutbox(eventKey, "INVENTORY_RELEASE_REQUESTED", orderId,
				Map.of("orderId", orderId, "reservationIds", reservationIds));
	}

	private void createGenericOutbox(String eventKey, String eventType, Long orderId, Map<String, Object> payload) {
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
					orderId,
					objectMapper.writeValueAsString(payload),
					"NEW",
					0,
					"",
					null,
					null,
					null
			));
		} catch (JsonProcessingException ex) {
			throw new IllegalStateException("failed to serialize inventory release event", ex);
		}
	}
}

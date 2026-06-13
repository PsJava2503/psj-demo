package com.commerce.order.application.service;

import com.commerce.messaging.OrderCreatedEvent;
import com.commerce.order.application.port.InventoryCommandPort;
import com.commerce.order.application.port.NotificationCommandPort;
import com.commerce.order.application.port.PaymentCommandPort;
import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderInventoryReservation;
import com.commerce.order.domain.model.OrderInventoryReservationQueryOptions;
import com.commerce.order.domain.model.OrderOutboxEventQueryOptions;
import com.commerce.order.domain.model.OrderQueryOptions;
import com.commerce.order.domain.model.OrderStatus;
import com.commerce.order.domain.model.OrderStatusLog;
import com.commerce.order.domain.repository.OrderRepository;
import com.commerce.order.infrastructure.mq.OrderEventPublisher;
import com.commerce.order.infrastructure.persistence.OrderInventoryReservationRepository;
import com.commerce.order.infrastructure.persistence.OrderOutboxEventRepository;
import com.commerce.order.infrastructure.persistence.OrderStatusLogRepository;
import com.commerce.order.infrastructure.persistence.model.OrderOutboxEventData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxRelay {

	private static final Logger log = LoggerFactory.getLogger(OrderOutboxRelay.class);
	private static final String ORDER_CREATED = "ORDER_CREATED";
	private static final String ORDER_WAIT_PAY = "ORDER_WAIT_PAY";
	private static final String INVENTORY_CONFIRM_REQUESTED = "INVENTORY_CONFIRM_REQUESTED";
	private static final String INVENTORY_RELEASE_REQUESTED = "INVENTORY_RELEASE_REQUESTED";
	private static final String NOTIFICATION_REQUESTED = "NOTIFICATION_REQUESTED";
	private static final String REFUND_REQUESTED = "REFUND_REQUESTED";

	private final OrderOutboxEventRepository orderOutboxEventRepository;
	private final OrderEventPublisher orderEventPublisher;
	private final InventoryCommandPort inventoryCommandPort;
	private final NotificationCommandPort notificationCommandPort;
	private final PaymentCommandPort paymentCommandPort;
	private final OrderRepository orderRepository;
	private final OrderInventoryReservationRepository reservationRepository;
	private final OrderStatusLogRepository orderStatusLogRepository;
	private final ObjectMapper objectMapper;

	public OrderOutboxRelay(
			OrderOutboxEventRepository orderOutboxEventRepository,
			OrderEventPublisher orderEventPublisher,
			InventoryCommandPort inventoryCommandPort,
			NotificationCommandPort notificationCommandPort,
			PaymentCommandPort paymentCommandPort,
			OrderRepository orderRepository,
			OrderInventoryReservationRepository reservationRepository,
			OrderStatusLogRepository orderStatusLogRepository,
			ObjectMapper objectMapper
	) {
		this.orderOutboxEventRepository = orderOutboxEventRepository;
		this.orderEventPublisher = orderEventPublisher;
		this.inventoryCommandPort = inventoryCommandPort;
		this.notificationCommandPort = notificationCommandPort;
		this.paymentCommandPort = paymentCommandPort;
		this.orderRepository = orderRepository;
		this.reservationRepository = reservationRepository;
		this.orderStatusLogRepository = orderStatusLogRepository;
		this.objectMapper = objectMapper;
	}

	@Scheduled(fixedDelayString = "${order.outbox-relay-delay-ms:5000}")
	public void publishPendingEvents() {
		publishPendingEvents("NEW");
		publishPendingEvents("FAILED");
		publishPendingEvents("PUBLISHING");
	}

	private void publishPendingEvents(String status) {
		OrderOutboxEventQueryOptions options = new OrderOutboxEventQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.of(status),
				Optional.of(ZonedDateTime.now())
		);
		for (OrderOutboxEventData event : orderOutboxEventRepository.query(options)) {
			publishOne(event);
		}
	}

	private void publishOne(OrderOutboxEventData event) {
		if ("PUBLISHED".equals(event.status())) {
			return;
		}
		if (!claim(event)) {
			return;
		}
		try {
			if (ORDER_CREATED.equals(event.eventType()) || ORDER_WAIT_PAY.equals(event.eventType())) {
				orderEventPublisher.publishOrderCreated(objectMapper.readValue(event.payload(), OrderCreatedEvent.class));
				markPublished(event);
				return;
			}
			if (INVENTORY_CONFIRM_REQUESTED.equals(event.eventType())) {
				confirmInventory(event);
				markPublished(event);
				return;
			}
			if (INVENTORY_RELEASE_REQUESTED.equals(event.eventType())) {
				releaseInventory(event);
				markPublished(event);
				return;
			}
			if (NOTIFICATION_REQUESTED.equals(event.eventType())) {
				notifyOrder(event);
				markPublished(event);
				return;
			}
			if (REFUND_REQUESTED.equals(event.eventType())) {
				refundPayment(event);
				markPublished(event);
				return;
			}
			markPublished(event);
		} catch (Exception ex) {
			if (INVENTORY_CONFIRM_REQUESTED.equals(event.eventType())) {
				markInventoryConfirmFailed(event, ex);
			}
			markFailed(event, ex);
		}
	}

	private boolean claim(OrderOutboxEventData event) {
		int updated = orderOutboxEventRepository.update(new OrderOutboxEventData(
				null,
				null,
				null,
				null,
				null,
				"PUBLISHING",
				event.attemptCount(),
				event.lastError(),
				ZonedDateTime.now().plusSeconds(60),
				null,
				null
		), new OrderOutboxEventQueryOptions(
				Optional.ofNullable(event.id()),
				Optional.empty(),
				Optional.ofNullable(event.status()),
				Optional.empty()
		));
		return updated == 1;
	}

	private void confirmInventory(OrderOutboxEventData event) throws Exception {
		JsonNode payload = objectMapper.readTree(event.payload());
		Long orderId = payload.get("orderId").asLong();
		List<Long> reservationIds = reservationIds(payload);
		boolean canConfirmInventory = false;
		Optional<Order> beforeConfirm = orderRepository.query(byOrderId(orderId)).stream().findFirst();
		if (beforeConfirm.isPresent() && beforeConfirm.get().status() == OrderStatus.PAID) {
			Order order = beforeConfirm.get();
			OrderStatus fromStatus = order.status();
			order.markInventoryConfirming();
			if (orderRepository.update(order, byOrderIdAndStatus(orderId, fromStatus)) == 1) {
				logStatus(orderId, fromStatus, order.status(), "inventory confirming");
				canConfirmInventory = true;
			}
		}
		else if (beforeConfirm.isPresent() && beforeConfirm.get().status() == OrderStatus.INVENTORY_CONFIRMING) {
			canConfirmInventory = true;
		}
		if (!canConfirmInventory) {
			return;
		}
		if (!reservationIds.isEmpty()) {
			inventoryCommandPort.confirmStock(reservationIds, orderId);
			markReservations(orderId, "RESERVED", "CONFIRMED");
		}
		Optional<Order> order = orderRepository.query(byOrderId(orderId)).stream().findFirst();
		if (order.isPresent() && order.get().status() == OrderStatus.INVENTORY_CONFIRMING) {
			Order paidOrder = order.get();
			OrderStatus fromStatus = paidOrder.status();
			paidOrder.waitShip();
			if (orderRepository.update(paidOrder, byOrderIdAndStatus(orderId, fromStatus)) == 1) {
				logStatus(orderId, fromStatus, paidOrder.status(), "inventory confirmed");
			}
		}
	}

	private void markInventoryConfirmFailed(OrderOutboxEventData event, Exception ex) {
		try {
			JsonNode payload = objectMapper.readTree(event.payload());
			Long orderId = payload.get("orderId").asLong();
			Optional<Order> order = orderRepository.query(byOrderId(orderId)).stream().findFirst();
			if (order.isPresent() && order.get().status() == OrderStatus.INVENTORY_CONFIRMING) {
				Order failedOrder = order.get();
				OrderStatus fromStatus = failedOrder.status();
				failedOrder.markInventoryConfirmFailed();
				if (orderRepository.update(failedOrder, byOrderIdAndStatus(orderId, fromStatus)) == 1) {
					logStatus(orderId, fromStatus, failedOrder.status(), "inventory confirm failed: " + ex.getMessage());
				}
			}
		} catch (Exception ignored) {
			log.warn("Failed to mark inventory confirm failure for outbox event: {}", event.eventKey(), ignored);
		}
	}

	private void releaseInventory(OrderOutboxEventData event) throws Exception {
		JsonNode payload = objectMapper.readTree(event.payload());
		Long orderId = payload.get("orderId").asLong();
		List<Long> reservationIds = reservationIds(payload);
		if (!reservationIds.isEmpty()) {
			inventoryCommandPort.releaseStock(reservationIds, orderId);
			markReservations(orderId, "RESERVED", "RELEASED");
		}
	}

	private void notifyOrder(OrderOutboxEventData event) throws Exception {
		JsonNode payload = objectMapper.readTree(event.payload());
		String templateCode = payload.path("templateCode").asText("ORDER_PAID");
		if ("ORDER_WAIT_PAY".equals(templateCode)) {
			notificationCommandPort.notifyOrderWaitPay(payload.get("orderId").asLong());
			return;
		}
		if ("ORDER_CANCELLED".equals(templateCode)) {
			notificationCommandPort.notifyOrderCancelled(payload.get("orderId").asLong());
			return;
		}
		notificationCommandPort.notifyOrderPaid(payload.get("orderId").asLong());
	}

	private void refundPayment(OrderOutboxEventData event) throws Exception {
		JsonNode payload = objectMapper.readTree(event.payload());
		Long orderId = payload.get("orderId").asLong();
		BigDecimal amount = new BigDecimal(payload.get("amount").asText());
		String reason = payload.path("reason").asText("paid_after_order_closed");
		String result = paymentCommandPort.refund(orderId, amount, reason);
		if (!result.startsWith("REFUND_SUCCESS")) {
			throw new IllegalStateException("refund failed: " + result);
		}
		Optional<Order> order = orderRepository.query(byOrderId(orderId)).stream().findFirst();
		if (order.isPresent() && order.get().status() == OrderStatus.REFUND_REQUIRED) {
			Order refundedOrder = order.get();
			OrderStatus fromStatus = refundedOrder.status();
			refundedOrder.markRefunded();
			if (orderRepository.update(refundedOrder, byOrderIdAndStatus(orderId, fromStatus)) == 1) {
				logStatus(orderId, fromStatus, refundedOrder.status(), "refund success");
			}
		}
	}

	private List<Long> reservationIds(JsonNode payload) {
		List<Long> reservationIds = new ArrayList<>();
		JsonNode node = payload.get("reservationIds");
		if (node != null && node.isArray()) {
			node.forEach(value -> reservationIds.add(value.asLong()));
		}
		return reservationIds;
	}

	private void markReservations(Long orderId, String fromStatus, String toStatus) {
		reservationRepository.update(new OrderInventoryReservation(
				null,
				null,
				null,
				toStatus,
				null,
				null
		), new OrderInventoryReservationQueryOptions(
				Optional.empty(),
				Optional.ofNullable(orderId),
				Optional.empty(),
				Optional.of(fromStatus)
		));
	}

	private OrderQueryOptions byOrderId(Long orderId) {
		return new OrderQueryOptions(Optional.ofNullable(orderId), Optional.empty(), Optional.empty(), Optional.empty());
	}

	private OrderQueryOptions byOrderIdAndStatus(Long orderId, OrderStatus status) {
		return new OrderQueryOptions(Optional.ofNullable(orderId), Optional.empty(), Optional.ofNullable(status), Optional.empty());
	}

	private void logStatus(Long orderId, OrderStatus fromStatus, OrderStatus toStatus, String reason) {
		orderStatusLogRepository.create(new OrderStatusLog(null, orderId, fromStatus, toStatus, reason, null));
	}

	private void markPublished(OrderOutboxEventData event) {
		orderOutboxEventRepository.update(new OrderOutboxEventData(
				null,
				null,
				null,
				null,
				null,
				"PUBLISHED",
				nextAttemptCount(event),
				"",
				ZonedDateTime.now(),
				null,
				null
		), byId(event.id()));
	}

	private void markFailed(OrderOutboxEventData event, Exception ex) {
		int attemptCount = nextAttemptCount(event);
		orderOutboxEventRepository.update(new OrderOutboxEventData(
				null,
				null,
				null,
				null,
				null,
				"FAILED",
				attemptCount,
				ex.getMessage(),
				ZonedDateTime.now().plusSeconds(Math.min(300L, Math.max(5L, attemptCount * 10L))),
				null,
				null
		), byId(event.id()));
		log.warn("Failed to publish order outbox event: eventKey={}, attempt={}", event.eventKey(), attemptCount, ex);
	}

	private OrderOutboxEventQueryOptions byId(Long id) {
		return new OrderOutboxEventQueryOptions(Optional.ofNullable(id), Optional.empty(), Optional.empty(), Optional.empty());
	}

	private int nextAttemptCount(OrderOutboxEventData event) {
		return (event.attemptCount() == null ? 0 : event.attemptCount()) + 1;
	}
}

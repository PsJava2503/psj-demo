package com.commerce.order.application.service;

import com.commerce.address.AddressResponse;
import com.commerce.messaging.OrderCreatedEvent;
import com.commerce.order.application.command.CreateOrderCommand;
import com.commerce.order.application.port.AddressQueryPort;
import com.commerce.order.application.port.InventoryCommandPort;
import com.commerce.order.application.port.OrderUseCase;
import com.commerce.order.application.port.PaymentCommandPort;
import com.commerce.order.application.port.ProductQueryPort;
import com.commerce.order.application.port.UserQueryPort;
import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderInventoryReservation;
import com.commerce.order.domain.model.OrderInventoryReservationQueryOptions;
import com.commerce.order.domain.model.OrderItem;
import com.commerce.order.domain.model.OrderQueryOptions;
import com.commerce.order.domain.model.OrderStatus;
import com.commerce.order.domain.model.OrderStatusLog;
import com.commerce.order.domain.repository.OrderRepository;
import com.commerce.order.domain.service.OrderDomainService;
import com.commerce.order.infrastructure.persistence.OrderInventoryReservationRepository;
import com.commerce.order.infrastructure.persistence.OrderItemRepository;
import com.commerce.order.infrastructure.persistence.OrderOutboxEventRepository;
import com.commerce.order.infrastructure.persistence.OrderStatusLogRepository;
import com.commerce.order.infrastructure.persistence.model.OrderOutboxEventData;
import com.commerce.product.ProductResponse;
import com.commerce.user.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderApplicationService implements OrderUseCase {

	private final OrderDomainService orderDomainService;
	private final OrderRepository orderRepository;
	private final UserQueryPort userQueryPort;
	private final ProductQueryPort productQueryPort;
	private final AddressQueryPort addressQueryPort;
	private final InventoryCommandPort inventoryCommandPort;
	private final PaymentCommandPort paymentCommandPort;
	private final OrderInventoryReservationRepository reservationRepository;
	private final OrderItemRepository orderItemRepository;
	private final OrderStatusLogRepository orderStatusLogRepository;
	private final OrderOutboxEventRepository orderOutboxEventRepository;
	private final ObjectMapper objectMapper;

	public OrderApplicationService(
			OrderDomainService orderDomainService,
			OrderRepository orderRepository,
			UserQueryPort userQueryPort,
			ProductQueryPort productQueryPort,
			AddressQueryPort addressQueryPort,
			InventoryCommandPort inventoryCommandPort,
			PaymentCommandPort paymentCommandPort,
			OrderInventoryReservationRepository reservationRepository,
			OrderItemRepository orderItemRepository,
			OrderStatusLogRepository orderStatusLogRepository,
			OrderOutboxEventRepository orderOutboxEventRepository,
			ObjectMapper objectMapper
	) {
		this.orderDomainService = orderDomainService;
		this.orderRepository = orderRepository;
		this.userQueryPort = userQueryPort;
		this.productQueryPort = productQueryPort;
		this.addressQueryPort = addressQueryPort;
		this.inventoryCommandPort = inventoryCommandPort;
		this.paymentCommandPort = paymentCommandPort;
		this.reservationRepository = reservationRepository;
		this.orderItemRepository = orderItemRepository;
		this.orderStatusLogRepository = orderStatusLogRepository;
		this.orderOutboxEventRepository = orderOutboxEventRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	@Transactional
	public String create(CreateOrderCommand command) {
		UserResponse user = userQueryPort.getUser(command.userId());
		ProductResponse product = productQueryPort.getProduct(command.productId());
		AddressResponse address = command.addressId() == null ? null : addressQueryPort.getAddress(command.addressId());
		Order order = orderDomainService.create(command.userId(), product, address, command.quantity());
		orderRepository.create(order);
		createOrderItem(order);
		logStatus(order.orderId(), null, order.status(), "order created");

		List<Long> reservationIds = inventoryCommandPort.reserveStock(order.skuId(), order.quantity(), order.orderId());
		if (reservationIds.isEmpty()) {
			OrderStatus fromStatus = order.status();
			order.fail();
			if (orderRepository.update(order, byOrderIdAndStatus(order.orderId(), fromStatus)) == 1) {
				logStatus(order.orderId(), fromStatus, order.status(), "insufficient stock");
			}
			return "Order " + order.orderId() + " failed: insufficient stock";
		}
		saveReservations(order.orderId(), reservationIds);

		OrderStatus fromStatus = order.status();
		order.markStockReserved();
		logStatus(order.orderId(), fromStatus, order.status(), "stock reserved");
		fromStatus = order.status();
		order.waitPay();
		if (orderRepository.update(order, byOrderIdAndStatus(order.orderId(), fromStatus)) == 1) {
			logStatus(order.orderId(), fromStatus, order.status(), "waiting payment");
		}
		String paymentStatus = paymentCommandPort.preCreate(
				order.orderId(),
				order.amount(),
				"order-" + order.orderNo()
		);
		if (paymentStatus.startsWith("PAY_FAILED")) {
			fromStatus = order.status();
			order.fail();
			if (orderRepository.update(order, byOrderIdAndStatus(order.orderId(), fromStatus)) == 1) {
				logStatus(order.orderId(), fromStatus, order.status(), "payment precreate failed");
				createInventoryReleaseOutbox("inventory-release-payment-failed:" + order.orderId(), order.orderId(), reservationIds);
			}
		}
		else {
			createOrderWaitPayOutbox(order);
			createGenericOutbox(
					"notification-order-wait-pay:" + order.orderId(),
					"NOTIFICATION_REQUESTED",
					order.orderId(),
					java.util.Map.of("orderId", order.orderId(), "templateCode", "ORDER_WAIT_PAY")
			);
		}

		return "Order " + order.orderId() + " created for " + fullNameOf(user) + ", amount=" + order.amount() + ", payment=" + paymentStatus;
	}

	@Override
	@Transactional
	public int cancel(Long orderId) {
		Optional<Order> existing = orderRepository.query(byOrderId(orderId)).stream().findFirst();
		if (existing.isEmpty()) {
			return 0;
		}
		Order order = existing.get();
		if (order.status() == OrderStatus.COMPLETED || order.status() == OrderStatus.CANCELLED) {
			return 0;
		}
		List<Long> reservationIds = reservationIdsOf(orderId, "RESERVED");
		OrderStatus fromStatus = order.status();
		order.cancel();
		int rows = orderRepository.update(order, byOrderIdAndStatus(orderId, fromStatus));
		if (rows != 1) {
			return rows;
		}
		logStatus(orderId, fromStatus, order.status(), "order cancelled");
		createOrderCancelledOutbox(order);
		createGenericOutbox(
				"notification-order-cancelled:" + orderId,
				"NOTIFICATION_REQUESTED",
				orderId,
				java.util.Map.of("orderId", orderId, "templateCode", "ORDER_CANCELLED")
		);
		createInventoryReleaseOutbox("inventory-release-cancelled:" + orderId, orderId, reservationIds);
		return rows;
	}

	@Override
	public List<Order> query(OrderQueryOptions options) {
		return orderRepository.query(options);
	}

	@Override
	@Transactional
	public int ship(Long orderId) {
		return transit(orderId, OrderStatus.WAIT_SHIP, "order shipped", Order::ship);
	}

	@Override
	@Transactional
	public int receive(Long orderId) {
		return transit(orderId, OrderStatus.SHIPPED, "order received", Order::receive);
	}

	@Override
	@Transactional
	public int complete(Long orderId) {
		return transit(orderId, OrderStatus.RECEIVED, "order completed", Order::complete);
	}

	private int transit(Long orderId, OrderStatus expectedStatus, String reason, java.util.function.Consumer<Order> transition) {
		Optional<Order> existing = orderRepository.query(byOrderId(orderId)).stream().findFirst();
		if (existing.isEmpty() || existing.get().status() != expectedStatus) {
			return 0;
		}
		Order order = existing.get();
		OrderStatus fromStatus = order.status();
		transition.accept(order);
		int rows = orderRepository.update(order, byOrderIdAndStatus(orderId, fromStatus));
		if (rows == 1) {
			logStatus(orderId, fromStatus, order.status(), reason);
		}
		return rows;
	}

	private OrderQueryOptions byOrderId(Long orderId) {
		return new OrderQueryOptions(
				Optional.ofNullable(orderId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	private OrderQueryOptions byOrderIdAndStatus(Long orderId, OrderStatus status) {
		return new OrderQueryOptions(
				Optional.ofNullable(orderId),
				Optional.empty(),
				Optional.ofNullable(status),
				Optional.empty()
		);
	}

	private void saveReservations(Long orderId, List<Long> reservationIds) {
		for (Long reservationId : reservationIds) {
			reservationRepository.create(new OrderInventoryReservation(
					null,
					orderId,
					reservationId,
					"RESERVED",
					null,
					null
			));
		}
	}

	private void createOrderItem(Order order) {
		orderItemRepository.create(new OrderItem(
				null,
				order.orderId(),
				order.productId(),
				order.skuId(),
				order.productName(),
				order.unitPrice(),
				order.quantity(),
				order.amount(),
				null
		));
	}

	private void logStatus(Long orderId, OrderStatus fromStatus, OrderStatus toStatus, String reason) {
		orderStatusLogRepository.create(new OrderStatusLog(
				null,
				orderId,
				fromStatus,
				toStatus,
				reason,
				null
		));
	}

	private List<Long> reservationIdsOf(Long orderId, String status) {
		return reservationRepository.query(new OrderInventoryReservationQueryOptions(
				Optional.empty(),
				Optional.ofNullable(orderId),
				Optional.empty(),
				Optional.ofNullable(status)
		)).stream()
				.map(OrderInventoryReservation::reservationId)
				.toList();
	}

	private void createOrderWaitPayOutbox(Order order) {
		try {
			orderOutboxEventRepository.create(new OrderOutboxEventData(
					null,
					"order-wait-pay:" + order.orderId(),
					"ORDER_WAIT_PAY",
					order.orderId(),
					objectMapper.writeValueAsString(new OrderCreatedEvent(
							order.orderId(),
							order.userId(),
							order.productId(),
							order.quantity(),
							order.amount()
					)),
					"NEW",
					0,
					"",
					null,
					null,
					null
			));
		} catch (JsonProcessingException ex) {
			throw new IllegalStateException("failed to serialize order-created event", ex);
		}
	}

	private void createInventoryReleaseOutbox(String eventKey, Long orderId, List<Long> reservationIds) {
		if (reservationIds.isEmpty() || !orderOutboxEventRepository.query(new com.commerce.order.domain.model.OrderOutboxEventQueryOptions(
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
					"INVENTORY_RELEASE_REQUESTED",
					orderId,
					objectMapper.writeValueAsString(java.util.Map.of("orderId", orderId, "reservationIds", reservationIds)),
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

	private void createOrderCancelledOutbox(Order order) {
		createGenericOutbox(
				"order-cancelled:" + order.orderId(),
				"ORDER_CANCELLED",
				order.orderId(),
				java.util.Map.of("orderId", order.orderId(), "userId", order.userId())
		);
	}

	private void createGenericOutbox(String eventKey, String eventType, Long aggregateId, java.util.Map<String, Object> payload) {
		if (!orderOutboxEventRepository.query(new com.commerce.order.domain.model.OrderOutboxEventQueryOptions(
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

	private String fullNameOf(UserResponse user) {
		String firstName = user.firstName() == null ? "" : user.firstName().trim();
		String secondName = user.secondName() == null ? "" : user.secondName().trim();
		String fullName = (firstName + " " + secondName).trim();
		return fullName.isBlank() ? "unknown-user" : fullName;
	}

}

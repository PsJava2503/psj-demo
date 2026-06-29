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
import com.commerce.order.domain.model.OrderRefundRequest;
import com.commerce.order.domain.model.OrderStatus;
import com.commerce.order.domain.model.OrderStatusLog;
import com.commerce.order.domain.model.OrderSubOrder;
import com.commerce.order.domain.repository.OrderRepository;
import com.commerce.order.domain.service.OrderDomainService;
import com.commerce.order.infrastructure.persistence.OrderInventoryReservationRepository;
import com.commerce.order.infrastructure.persistence.OrderItemRepository;
import com.commerce.order.infrastructure.persistence.OrderOutboxEventRepository;
import com.commerce.order.infrastructure.persistence.OrderRefundRequestRepository;
import com.commerce.order.infrastructure.persistence.OrderStatusLogRepository;
import com.commerce.order.infrastructure.persistence.OrderSubOrderRepository;
import com.commerce.order.infrastructure.persistence.model.OrderOutboxEventData;
import com.commerce.payment.PaymentAllocationRequest;
import com.commerce.payment.PreCreatePaymentRequest;
import com.commerce.payment.RefundAllocationRequest;
import com.commerce.product.ProductResponse;
import com.commerce.user.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
	private final OrderSubOrderRepository orderSubOrderRepository;
	private final OrderRefundRequestRepository orderRefundRequestRepository;
	private final ObjectMapper objectMapper;
	private final TransactionTemplate transactionTemplate;

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
			OrderSubOrderRepository orderSubOrderRepository,
			OrderRefundRequestRepository orderRefundRequestRepository,
			ObjectMapper objectMapper,
			TransactionTemplate transactionTemplate
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
		this.orderSubOrderRepository = orderSubOrderRepository;
		this.orderRefundRequestRepository = orderRefundRequestRepository;
		this.objectMapper = objectMapper;
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public String create(CreateOrderCommand command) {
		UserResponse user = userQueryPort.getUser(command.userId());
		AddressResponse address = command.addressId() == null ? null : addressQueryPort.getAddress(command.addressId());
		List<CheckoutLine> checkoutLines = checkoutLines(command);
		ProductResponse primaryProduct = checkoutLines.get(0).product();
		BigDecimal totalAmount = checkoutLines.stream()
				.map(CheckoutLine::amount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		int totalQuantity = checkoutLines.stream()
				.mapToInt(CheckoutLine::quantity)
				.sum();
		Order order = orderDomainService.createCheckout(command.userId(), primaryProduct, address, totalQuantity, totalAmount);
		List<Long> reservationIds = reserveStock(order, checkoutLines);
		if (reservationIds.isEmpty()) {
			return "Order " + order.orderId() + " failed: insufficient stock";
		}
		CreateOrderResult created = transactionTemplate.execute(status -> createOrderAfterStockReserved(order, checkoutLines, reservationIds));
		try {
			inventoryCommandPort.bindReservations(reservationIds, order.orderId());
		} catch (Exception ex) {
			transactionTemplate.executeWithoutResult(status -> markPaymentPrecreateFailed(order.orderId(), reservationIds));
			return "Order " + order.orderId() + " failed: inventory reservation bind failed";
		}
		String paymentStatus = paymentCommandPort.preCreateDetailed(new PreCreatePaymentRequest(
				order.orderId(),
				order.amount(),
				"order-" + order.orderNo(),
				created.subOrders().stream()
						.map(subOrder -> new PaymentAllocationRequest(
								subOrder.id(),
								subOrder.merchantId(),
								subOrder.amount(),
								BigDecimal.ZERO,
								BigDecimal.ZERO,
								BigDecimal.ZERO,
								subOrder.amount(),
								subOrder.amount()
						))
						.toList()
		));
		if (paymentStatus.startsWith("PAY_FAILED")) {
			transactionTemplate.executeWithoutResult(status -> markPaymentPrecreateFailed(order.orderId(), reservationIds));
		}
		else {
			transactionTemplate.executeWithoutResult(status -> createWaitPaySideEffects(order));
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
		createGenericOutbox(
				"payment-close-cancelled:" + orderId,
				"PAYMENT_CLOSE_REQUESTED",
				orderId,
				java.util.Map.of("orderId", orderId, "reason", "order_cancelled")
		);
		orderSubOrderRepository.updateStatusForCheckoutOrder(orderId, "CANCELLED");
		return rows;
	}

	@Override
	@Transactional
	public String refund(Long orderId, Long subOrderId, BigDecimal amount, String reason) {
		Order order = orderRepository.query(byOrderId(orderId)).stream()
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("order not found"));
		BigDecimal refundAmount = amount == null ? refundAmountOf(order, subOrderId) : amount;
		if (refundAmount.signum() <= 0) {
			throw new IllegalArgumentException("invalid refund amount");
		}
		String refundRequestNo = "MANUAL-" + orderId + "-" + System.currentTimeMillis();
		orderRefundRequestRepository.create(new OrderRefundRequest(
				null,
				refundRequestNo,
				orderId,
				subOrderId,
				refundAmount,
				reason,
				"REQUESTED",
				null,
				null,
				null
		));
		if (subOrderId != null) {
			orderSubOrderRepository.updateRefundStatus(subOrderId, "REQUESTED");
		}
		else if (refundAmount.compareTo(order.amount()) == 0) {
			OrderStatus fromStatus = order.status();
			order.requireRefund();
			if (orderRepository.update(order, byOrderIdAndStatus(orderId, fromStatus)) == 1) {
				logStatus(orderId, fromStatus, order.status(), "refund requested");
			}
		}
		createGenericOutbox(
				"refund-requested:" + refundRequestNo,
				"REFUND_REQUESTED",
				orderId,
				java.util.Map.of(
						"orderId", orderId,
						"refundRequestNo", refundRequestNo,
						"amount", refundAmount,
						"reason", reason == null ? "manual_refund" : reason,
						"allocations", refundAllocations(order, subOrderId, refundAmount)
				)
		);
		return "REFUND_REQUESTED:" + refundRequestNo;
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

	private List<CheckoutLine> checkoutLines(CreateOrderCommand command) {
		return command.items().stream()
				.map(item -> {
					ProductResponse product = productQueryPort.getProduct(item.productId());
					if (product.price() == null || product.price().compareTo(BigDecimal.ZERO) < 0) {
						throw new IllegalArgumentException("invalid product price");
					}
					if (item.quantity() == null || item.quantity() <= 0) {
						throw new IllegalArgumentException("invalid order quantity");
					}
					BigDecimal amount = product.price().multiply(BigDecimal.valueOf(item.quantity()));
					return new CheckoutLine(product, item.quantity(), amount);
				})
				.toList();
	}

	private List<OrderSubOrder> createSubOrders(Order order, List<CheckoutLine> checkoutLines) {
		return checkoutLines.stream()
				.map(line -> {
					OrderSubOrder subOrder = orderSubOrderRepository.create(new OrderSubOrder(
							null,
							order.orderId(),
							line.product().id(),
							line.product().skuId(),
							line.product().name(),
							line.product().price(),
							line.quantity(),
							line.amount(),
							null,
							"CREATED",
							"NONE",
							null,
							null
					));
					createOrderItem(subOrder);
					return subOrder;
				})
				.toList();
	}

	private CreateOrderResult createOrderAfterStockReserved(Order order, List<CheckoutLine> checkoutLines, List<Long> reservationIds) {
		orderRepository.create(order);
		List<OrderSubOrder> subOrders = createSubOrders(order, checkoutLines);
		logStatus(order.orderId(), null, order.status(), "order created");
		saveReservations(order.orderId(), reservationIds);

		OrderStatus fromStatus = order.status();
		order.markStockReserved();
		if (orderRepository.update(order, byOrderIdAndStatus(order.orderId(), fromStatus)) == 1) {
			logStatus(order.orderId(), fromStatus, order.status(), "stock reserved");
		}
		fromStatus = order.status();
		order.waitPay();
		if (orderRepository.update(order, byOrderIdAndStatus(order.orderId(), fromStatus)) == 1) {
			logStatus(order.orderId(), fromStatus, order.status(), "waiting payment");
		}
		orderSubOrderRepository.updateStatusForCheckoutOrder(order.orderId(), "WAIT_PAY");
		return new CreateOrderResult(subOrders);
	}

	private List<Long> reserveStock(Order order, List<CheckoutLine> checkoutLines) {
		List<Long> reservationIds = new java.util.ArrayList<>();
		for (CheckoutLine line : checkoutLines) {
			List<Long> lineReservationIds = inventoryCommandPort.reserveStock(line.product().skuId(), line.quantity(), order.orderId());
			if (lineReservationIds.isEmpty()) {
				return List.of();
			}
			reservationIds.addAll(lineReservationIds);
		}
		return reservationIds;
	}

	private void markPaymentPrecreateFailed(Long orderId, List<Long> reservationIds) {
		Order order = orderRepository.query(byOrderId(orderId)).stream()
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("order not found"));
		OrderStatus fromStatus = order.status();
		order.fail();
		if (orderRepository.update(order, byOrderIdAndStatus(order.orderId(), fromStatus)) == 1) {
			logStatus(order.orderId(), fromStatus, order.status(), "payment precreate failed");
			createInventoryReleaseOutbox("inventory-release-payment-failed:" + order.orderId(), order.orderId(), reservationIds);
		}
		orderSubOrderRepository.updateStatusForCheckoutOrder(order.orderId(), "FAILED");
	}

	private void createWaitPaySideEffects(Order order) {
		createOrderWaitPayOutbox(order);
		createGenericOutbox(
				"notification-order-wait-pay:" + order.orderId(),
				"NOTIFICATION_REQUESTED",
				order.orderId(),
				java.util.Map.of("orderId", order.orderId(), "templateCode", "ORDER_WAIT_PAY")
		);
	}

	private BigDecimal refundAmountOf(Order order, Long subOrderId) {
		if (subOrderId == null) {
			return order.amount();
		}
		return orderSubOrderRepository.findByCheckoutOrderId(order.orderId()).stream()
				.filter(subOrder -> subOrder.id().equals(subOrderId))
				.findFirst()
				.map(OrderSubOrder::amount)
				.orElseThrow(() -> new IllegalArgumentException("sub order not found"));
	}

	private List<RefundAllocationRequest> refundAllocations(Order order, Long subOrderId, BigDecimal refundAmount) {
		if (subOrderId != null) {
			return List.of(new RefundAllocationRequest(
					subOrderId,
					refundAmount,
					BigDecimal.ZERO,
					BigDecimal.ZERO,
					BigDecimal.ZERO,
					refundAmount
			));
		}
		List<OrderSubOrder> subOrders = orderSubOrderRepository.findByCheckoutOrderId(order.orderId());
		if (subOrders.isEmpty()) {
			return List.of(new RefundAllocationRequest(
					order.orderId(),
					refundAmount,
					BigDecimal.ZERO,
					BigDecimal.ZERO,
					BigDecimal.ZERO,
					refundAmount
			));
		}
		return subOrders.stream()
				.map(subOrder -> new RefundAllocationRequest(
						subOrder.id(),
						subOrder.amount(),
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						subOrder.amount()
				))
				.toList();
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

	private void createOrderItem(OrderSubOrder subOrder) {
		orderItemRepository.create(new OrderItem(
				null,
				subOrder.checkoutOrderId(),
				subOrder.productId(),
				subOrder.skuId(),
				subOrder.productName(),
				subOrder.unitPrice(),
				subOrder.quantity(),
				subOrder.amount(),
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

	private record CheckoutLine(ProductResponse product, Integer quantity, BigDecimal amount) {
	}

	private record CreateOrderResult(List<OrderSubOrder> subOrders) {
	}

}

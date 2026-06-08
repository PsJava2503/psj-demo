package com.psj.commerce.order.application.service;

import com.psj.commerce.order.application.command.CreateOrderCommand;
import com.psj.commerce.order.application.port.InventoryCommandPort;
import com.psj.commerce.order.application.port.NotificationCommandPort;
import com.psj.commerce.order.application.port.OrderUseCase;
import com.psj.commerce.order.application.port.PaymentCommandPort;
import com.psj.commerce.order.application.port.ProductQueryPort;
import com.psj.commerce.order.application.port.UserQueryPort;
import com.psj.commerce.order.domain.model.Order;
import com.psj.commerce.order.domain.repository.OrderRepository;
import com.psj.commerce.order.domain.service.OrderDomainService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class OrderApplicationService implements OrderUseCase {

	private final OrderDomainService orderDomainService;
	private final OrderRepository orderRepository;
	private final UserQueryPort userQueryPort;
	private final ProductQueryPort productQueryPort;
	private final InventoryCommandPort inventoryCommandPort;
	private final PaymentCommandPort paymentCommandPort;
	private final NotificationCommandPort notificationCommandPort;

	public OrderApplicationService(
			OrderDomainService orderDomainService,
			OrderRepository orderRepository,
			UserQueryPort userQueryPort,
			ProductQueryPort productQueryPort,
			InventoryCommandPort inventoryCommandPort,
			PaymentCommandPort paymentCommandPort,
			NotificationCommandPort notificationCommandPort
	) {
		this.orderDomainService = orderDomainService;
		this.orderRepository = orderRepository;
		this.userQueryPort = userQueryPort;
		this.productQueryPort = productQueryPort;
		this.inventoryCommandPort = inventoryCommandPort;
		this.paymentCommandPort = paymentCommandPort;
		this.notificationCommandPort = notificationCommandPort;
	}

	@Override
	public String create(CreateOrderCommand command) {
		String userName = userQueryPort.getUserName(command.userId());
		BigDecimal price = productQueryPort.getPrice(command.productId());
		Order order = orderDomainService.create(command.userId(), command.productId(), command.quantity(), price);

		if (!inventoryCommandPort.deductStock(order.productId(), order.quantity())) {
			order.fail();
			orderRepository.save(order);
			return "Order " + order.orderId() + " failed: insufficient stock";
		}

		order.markStockDeducted();
		String paymentStatus = paymentCommandPort.pay(order.orderId(), order.amount());
		order.markPaid();
		notificationCommandPort.notifyOrderPaid(order.orderId());
		order.complete();
		orderRepository.save(order);

		return "Order " + order.orderId() + " created for " + userName + ", amount=" + order.amount() + ", payment=" + paymentStatus;
	}

}

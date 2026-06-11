package com.commerce.order.application.service;

import com.commerce.order.application.command.CreateOrderCommand;
import com.commerce.order.application.port.InventoryCommandPort;
import com.commerce.order.application.port.OrderUseCase;
import com.commerce.order.application.port.PaymentCommandPort;
import com.commerce.order.application.port.ProductQueryPort;
import com.commerce.order.application.port.UserQueryPort;
import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.repository.OrderRepository;
import com.commerce.order.domain.service.OrderDomainService;
import com.commerce.user.UserResponse;
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

	public OrderApplicationService(
			OrderDomainService orderDomainService,
			OrderRepository orderRepository,
			UserQueryPort userQueryPort,
			ProductQueryPort productQueryPort,
			InventoryCommandPort inventoryCommandPort,
			PaymentCommandPort paymentCommandPort
	) {
		this.orderDomainService = orderDomainService;
		this.orderRepository = orderRepository;
		this.userQueryPort = userQueryPort;
		this.productQueryPort = productQueryPort;
		this.inventoryCommandPort = inventoryCommandPort;
		this.paymentCommandPort = paymentCommandPort;
	}

	@Override
	public String create(CreateOrderCommand command) {
		UserResponse user = userQueryPort.getUser(command.userId());
		BigDecimal price = productQueryPort.getPrice(command.productId());
		Order order = orderDomainService.create(command.userId(), command.productId(), command.quantity(), price);

		if (!inventoryCommandPort.deductStock(order.productId(), order.quantity())) {
			order.fail();
			orderRepository.save(order);
			return "Order " + order.orderId() + " failed: insufficient stock";
		}

		order.markStockDeducted();
		order.waitPay();
		orderRepository.save(order);
		String paymentStatus = paymentCommandPort.preCreate(
				order.orderId(),
				order.amount(),
				"order-" + order.orderId()
		);
		if (paymentStatus.startsWith("PAY_FAILED")) {
			order.fail();
			orderRepository.save(order);
		}

		return "Order " + order.orderId() + " created for " + fullNameOf(user) + ", amount=" + order.amount() + ", payment=" + paymentStatus;
	}

	private String fullNameOf(UserResponse user) {
		String firstName = user.firstName() == null ? "" : user.firstName().trim();
		String secondName = user.secondName() == null ? "" : user.secondName().trim();
		String fullName = (firstName + " " + secondName).trim();
		return fullName.isBlank() ? "unknown-user" : fullName;
	}

}

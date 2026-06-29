package com.commerce.order.application.port;

import com.commerce.order.application.command.CreateOrderCommand;
import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderQueryOptions;
import java.util.List;

public interface OrderUseCase {

	String create(CreateOrderCommand command);

	int cancel(Long orderId);

	String refund(Long orderId, Long subOrderId, java.math.BigDecimal amount, String reason);

	int ship(Long orderId);

	int receive(Long orderId);

	int complete(Long orderId);

	List<Order> query(OrderQueryOptions options);

}

package com.psj.commerce.order.interfaces.rest;

import com.psj.commerce.order.application.command.CreateOrderCommand;
import com.psj.commerce.order.application.port.OrderUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderUseCase orderUseCase;

	public OrderController(OrderUseCase orderUseCase) {
		this.orderUseCase = orderUseCase;
	}

	@PostMapping
	public String create(@RequestBody CreateOrderRequest request) {
		return orderUseCase.create(new CreateOrderCommand(request.userId(), request.productId(), request.quantity()));
	}

}

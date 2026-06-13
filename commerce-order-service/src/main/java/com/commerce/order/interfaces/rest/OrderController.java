package com.commerce.order.interfaces.rest;

import com.commerce.order.application.command.CreateOrderCommand;
import com.commerce.order.application.port.OrderUseCase;
import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderQueryOptions;
import com.commerce.order.domain.model.OrderStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
		return orderUseCase.create(new CreateOrderCommand(request.userId(), request.productId(), request.addressId(), request.quantity()));
	}

	@GetMapping
	public List<Order> query(
			@RequestParam Optional<Long> orderId,
			@RequestParam Optional<Long> userId,
			@RequestParam Optional<OrderStatus> status,
			@RequestParam Optional<ZonedDateTime> createTimeBefore
	) {
		return orderUseCase.query(new OrderQueryOptions(orderId, userId, status, createTimeBefore));
	}

	@GetMapping("/{orderId}")
	public Optional<Order> get(@PathVariable Long orderId) {
		return orderUseCase.query(new OrderQueryOptions(
				Optional.of(orderId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		)).stream().findFirst();
	}

	@DeleteMapping("/{orderId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancel(@PathVariable Long orderId) {
		orderUseCase.cancel(orderId);
	}

	@PostMapping("/{orderId}/ship")
	public int ship(@PathVariable Long orderId) {
		return orderUseCase.ship(orderId);
	}

	@PostMapping("/{orderId}/receive")
	public int receive(@PathVariable Long orderId) {
		return orderUseCase.receive(orderId);
	}

	@PostMapping("/{orderId}/complete")
	public int complete(@PathVariable Long orderId) {
		return orderUseCase.complete(orderId);
	}

}

package com.commerce.order.infrastructure.persistence;

import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderQueryOptions;
import com.commerce.order.domain.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

	private final Map<Long, Order> store = new ConcurrentHashMap<>();

	@Override
	public void save(Order order) {
		store.put(order.orderId(), order);
	}

	@Override
	public List<Order> query(OrderQueryOptions options) {
		OrderQueryOptions safeOptions = options == null ? OrderQueryOptions.none() : options;
		return store.values().stream()
				.filter(order -> safeOptions.getOrderId().map(order.orderId()::equals).orElse(true))
				.filter(order -> safeOptions.getStatus().map(order.status()::equals).orElse(true))
				.toList();
	}

}

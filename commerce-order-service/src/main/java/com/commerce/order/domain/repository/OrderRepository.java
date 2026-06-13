package com.commerce.order.domain.repository;

import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderQueryOptions;
import java.util.List;

public interface OrderRepository {

	int create(Order order);

	int update(Order order, OrderQueryOptions options);

	int delete(OrderQueryOptions options);

	List<Order> query(OrderQueryOptions options);

}

package com.commerce.order.domain.repository;

import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderQueryOptions;
import java.util.List;

public interface OrderRepository {

	void save(Order order);

	List<Order> query(OrderQueryOptions options);

}

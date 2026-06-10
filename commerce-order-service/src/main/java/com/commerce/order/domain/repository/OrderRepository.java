package com.commerce.order.domain.repository;

import com.commerce.order.domain.model.Order;

public interface OrderRepository {

	void save(Order order);

}

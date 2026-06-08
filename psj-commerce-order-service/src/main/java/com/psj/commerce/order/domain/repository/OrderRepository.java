package com.psj.commerce.order.domain.repository;

import com.psj.commerce.order.domain.model.Order;

public interface OrderRepository {

	void save(Order order);

}

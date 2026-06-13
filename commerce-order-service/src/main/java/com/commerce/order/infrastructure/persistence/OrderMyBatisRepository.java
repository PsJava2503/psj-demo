package com.commerce.order.infrastructure.persistence;

import com.commerce.order.domain.model.Order;
import com.commerce.order.domain.model.OrderQueryOptions;
import com.commerce.order.domain.model.OrderStatus;
import com.commerce.order.domain.repository.OrderRepository;
import com.commerce.order.infrastructure.persistence.mapper.OrderDynamicMapper;
import com.commerce.order.infrastructure.persistence.model.OrderData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OrderMyBatisRepository implements OrderRepository {

	private final OrderDynamicMapper orderDynamicMapper;

	public OrderMyBatisRepository(OrderDynamicMapper orderDynamicMapper) {
		this.orderDynamicMapper = orderDynamicMapper;
	}

	@Override
	public int create(Order order) {
		return orderDynamicMapper.create(toData(order));
	}

	@Override
	public int update(Order order, OrderQueryOptions options) {
		return orderDynamicMapper.update(toData(order), options);
	}

	@Override
	public int delete(OrderQueryOptions options) {
		return orderDynamicMapper.delete(options);
	}

	@Override
	public List<Order> query(OrderQueryOptions options) {
		return orderDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private Order toDomain(OrderData data) {
		return new Order(
				data.id(),
				data.orderNo(),
				data.userId(),
				data.productId(),
				data.skuId(),
				data.productName(),
				data.unitPrice(),
				data.quantity(),
				data.amount(),
				data.addressId(),
				data.recipientName(),
				data.recipientPhone(),
				data.province(),
				data.city(),
				data.district(),
				data.addressDetail(),
				data.version(),
				OrderStatus.valueOf(data.status()),
				data.createTime(),
				data.updateTime()
		);
	}

	private OrderData toData(Order order) {
		return new OrderData(
				order.orderId(),
				order.orderNo(),
				order.userId(),
				order.productId(),
				order.skuId(),
				order.productName(),
				order.unitPrice(),
				order.quantity(),
				order.amount(),
				order.addressId(),
				order.recipientName(),
				order.recipientPhone(),
				order.province(),
				order.city(),
				order.district(),
				order.addressDetail(),
				order.status().name(),
				order.version(),
				order.createTime(),
				order.updateTime()
		);
	}
}

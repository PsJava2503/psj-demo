package com.commerce.order.infrastructure.persistence;

import com.commerce.order.domain.model.OrderItem;
import com.commerce.order.domain.model.OrderItemQueryOptions;
import com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicMapper;
import com.commerce.order.infrastructure.persistence.model.OrderItemData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemRepository {

	private final OrderItemDynamicMapper mapper;

	public OrderItemRepository(OrderItemDynamicMapper mapper) {
		this.mapper = mapper;
	}

	public int create(OrderItem item) {
		return mapper.create(toData(item));
	}

	public int update(OrderItem item, OrderItemQueryOptions options) {
		return mapper.update(toData(item), options);
	}

	public int delete(OrderItemQueryOptions options) {
		return mapper.delete(options);
	}

	public List<OrderItem> query(OrderItemQueryOptions options) {
		return mapper.query(options).stream().map(this::toDomain).toList();
	}

	private OrderItem toDomain(OrderItemData data) {
		return new OrderItem(data.id(), data.orderId(), data.productId(), data.skuId(), data.productName(),
				data.unitPrice(), data.quantity(), data.amount(), data.createTime());
	}

	private OrderItemData toData(OrderItem item) {
		return new OrderItemData(item.id(), item.orderId(), item.productId(), item.skuId(), item.productName(),
				item.unitPrice(), item.quantity(), item.amount(), item.createTime());
	}
}

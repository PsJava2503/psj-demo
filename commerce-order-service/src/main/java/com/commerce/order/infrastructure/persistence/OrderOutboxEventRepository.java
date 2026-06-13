package com.commerce.order.infrastructure.persistence;

import com.commerce.order.domain.model.OrderOutboxEventQueryOptions;
import com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicMapper;
import com.commerce.order.infrastructure.persistence.model.OrderOutboxEventData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OrderOutboxEventRepository {

	private final OrderOutboxEventDynamicMapper mapper;

	public OrderOutboxEventRepository(OrderOutboxEventDynamicMapper mapper) {
		this.mapper = mapper;
	}

	public int create(OrderOutboxEventData data) {
		return mapper.create(data);
	}

	public int update(OrderOutboxEventData data, OrderOutboxEventQueryOptions options) {
		return mapper.update(data, options);
	}

	public int delete(OrderOutboxEventQueryOptions options) {
		return mapper.delete(options);
	}

	public List<OrderOutboxEventData> query(OrderOutboxEventQueryOptions options) {
		return mapper.query(options);
	}
}

package com.commerce.order.infrastructure.persistence;

import com.commerce.order.domain.model.OrderStatus;
import com.commerce.order.domain.model.OrderStatusLog;
import com.commerce.order.domain.model.OrderStatusLogQueryOptions;
import com.commerce.order.infrastructure.persistence.mapper.OrderStatusLogDynamicMapper;
import com.commerce.order.infrastructure.persistence.model.OrderStatusLogData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OrderStatusLogRepository {

	private final OrderStatusLogDynamicMapper mapper;

	public OrderStatusLogRepository(OrderStatusLogDynamicMapper mapper) {
		this.mapper = mapper;
	}

	public int create(OrderStatusLog log) {
		return mapper.create(toData(log));
	}

	public int update(OrderStatusLog log, OrderStatusLogQueryOptions options) {
		return mapper.update(toData(log), options);
	}

	public int delete(OrderStatusLogQueryOptions options) {
		return mapper.delete(options);
	}

	public List<OrderStatusLog> query(OrderStatusLogQueryOptions options) {
		return mapper.query(options).stream().map(this::toDomain).toList();
	}

	private OrderStatusLog toDomain(OrderStatusLogData data) {
		return new OrderStatusLog(data.id(), data.orderId(), toStatus(data.fromStatus()), toStatus(data.toStatus()),
				data.reason(), data.createTime());
	}

	private OrderStatusLogData toData(OrderStatusLog log) {
		return new OrderStatusLogData(log.id(), log.orderId(), statusName(log.fromStatus()),
				statusName(log.toStatus()), log.reason(), log.createTime());
	}

	private OrderStatus toStatus(String status) {
		return status == null ? null : OrderStatus.valueOf(status);
	}

	private String statusName(OrderStatus status) {
		return status == null ? null : status.name();
	}
}

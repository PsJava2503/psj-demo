package com.commerce.order.infrastructure.persistence;

import com.commerce.order.domain.model.OrderInventoryReservation;
import com.commerce.order.domain.model.OrderInventoryReservationQueryOptions;
import com.commerce.order.infrastructure.persistence.mapper.OrderInventoryReservationDynamicMapper;
import com.commerce.order.infrastructure.persistence.model.OrderInventoryReservationData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OrderInventoryReservationRepository {

	private final OrderInventoryReservationDynamicMapper mapper;

	public OrderInventoryReservationRepository(OrderInventoryReservationDynamicMapper mapper) {
		this.mapper = mapper;
	}

	public int create(OrderInventoryReservation reservation) {
		return mapper.create(toData(reservation));
	}

	public int update(OrderInventoryReservation reservation, OrderInventoryReservationQueryOptions options) {
		return mapper.update(toData(reservation), options);
	}

	public int delete(OrderInventoryReservationQueryOptions options) {
		return mapper.delete(options);
	}

	public List<OrderInventoryReservation> query(OrderInventoryReservationQueryOptions options) {
		return mapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private OrderInventoryReservation toDomain(OrderInventoryReservationData data) {
		return new OrderInventoryReservation(
				data.id(),
				data.orderId(),
				data.reservationId(),
				data.status(),
				data.createTime(),
				data.updateTime()
		);
	}

	private OrderInventoryReservationData toData(OrderInventoryReservation reservation) {
		return new OrderInventoryReservationData(
				reservation.id(),
				reservation.orderId(),
				reservation.reservationId(),
				reservation.status(),
				reservation.createTime(),
				reservation.updateTime()
		);
	}
}

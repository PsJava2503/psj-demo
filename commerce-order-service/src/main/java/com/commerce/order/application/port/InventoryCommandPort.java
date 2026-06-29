package com.commerce.order.application.port;

import java.util.List;

public interface InventoryCommandPort {

	List<Long> reserveStock(Long skuId, Integer quantity, Long orderId);

	void bindReservations(List<Long> reservationIds, Long orderId);

	void releaseStock(List<Long> reservationIds, Long orderId);

	void confirmStock(List<Long> reservationIds, Long orderId);

}

package com.commerce.order.infrastructure.rpc;

import com.commerce.order.application.port.InventoryCommandPort;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commerce-inventory-service")
public interface InventoryClient extends InventoryCommandPort {

	@Override
	@PostMapping("/internal/inventory/reserve")
	List<Long> reserveStock(
			@RequestParam("skuId") Long skuId,
			@RequestParam("quantity") Integer quantity,
			@RequestParam("orderId") Long orderId
	);

	@Override
	@PostMapping("/internal/inventory/reservations/bind")
	void bindReservations(@RequestBody List<Long> reservationIds, @RequestParam("orderId") Long orderId);

	@Override
	@PostMapping("/internal/inventory/release")
	void releaseStock(@RequestBody List<Long> reservationIds, @RequestParam("orderId") Long orderId);

	@Override
	@PostMapping("/internal/inventory/confirm")
	void confirmStock(@RequestBody List<Long> reservationIds, @RequestParam("orderId") Long orderId);

}

package com.commerce.inventory.interfaces.rest;

import com.commerce.inventory.application.port.InventoryUseCase;
import com.commerce.inventory.domain.model.InventoryModels.InboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.LockRequest;
import com.commerce.inventory.domain.model.InventoryModels.OutboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.UnlockRequest;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/inventory")
public class InternalInventoryController {

	private final InventoryUseCase inventoryUseCase;

	public InternalInventoryController(InventoryUseCase inventoryUseCase) {
		this.inventoryUseCase = inventoryUseCase;
	}

	@PostMapping("/deduct")
	public boolean deduct(@RequestParam Long productId, @RequestParam Integer quantity) {
		return inventoryUseCase.deductStock(productId, quantity);
	}

	@PostMapping("/reserve")
	public List<Long> reserve(@RequestParam Long skuId, @RequestParam Integer quantity, @RequestParam Long orderId) {
		return inventoryUseCase.reserveStock(skuId, quantity, orderId);
	}

	@PostMapping("/reservations/bind")
	public void bind(@RequestBody List<Long> reservationIds, @RequestParam Long orderId) {
		inventoryUseCase.bindReservations(reservationIds, orderId);
	}

	@PostMapping("/release")
	public void release(@RequestBody List<Long> reservationIds, @RequestParam Long orderId) {
		inventoryUseCase.releaseStock(reservationIds, orderId);
	}

	@PostMapping("/confirm")
	public void confirm(@RequestBody List<Long> reservationIds, @RequestParam Long orderId) {
		inventoryUseCase.confirmStock(reservationIds, orderId);
	}

	@PostMapping("/inbound")
	public void inbound(@RequestBody InboundRequest request) {
		inventoryUseCase.inbound(request);
	}

	@PostMapping("/outbound")
	public void outbound(@RequestBody OutboundRequest request) {
		inventoryUseCase.outbound(request);
	}

	@PostMapping("/lock")
	public List<Long> lock(@RequestBody LockRequest request) {
		return inventoryUseCase.lock(request);
	}

	@PostMapping("/unlock")
	public void unlock(@RequestBody UnlockRequest request) {
		inventoryUseCase.unlock(request);
	}
}

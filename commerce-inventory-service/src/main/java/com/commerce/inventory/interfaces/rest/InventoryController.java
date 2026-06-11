package com.commerce.inventory.interfaces.rest;

import com.commerce.inventory.application.port.InventoryUseCase;
import com.commerce.inventory.domain.model.InventoryModels.AdjustLockedRequest;
import com.commerce.inventory.domain.model.InventoryModels.AdjustRequest;
import com.commerce.inventory.domain.model.InventoryModels.Balance;
import com.commerce.inventory.domain.model.InventoryModels.Bin;
import com.commerce.inventory.domain.model.InventoryModels.BinBalance;
import com.commerce.inventory.domain.model.InventoryModels.CreateBinRequest;
import com.commerce.inventory.domain.model.InventoryModels.CreateReceiptRequest;
import com.commerce.inventory.domain.model.InventoryModels.CreateWarehouseRequest;
import com.commerce.inventory.domain.model.InventoryModels.InboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.InventoryReceipt;
import com.commerce.inventory.domain.model.InventoryModels.LockRequest;
import com.commerce.inventory.domain.model.InventoryModels.OutboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.PageResponse;
import com.commerce.inventory.domain.model.InventoryModels.TransitReceiptRequest;
import com.commerce.inventory.domain.model.InventoryModels.UnlockRequest;
import com.commerce.inventory.domain.model.InventoryModels.Warehouse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	private final InventoryUseCase inventoryUseCase;

	public InventoryController(InventoryUseCase inventoryUseCase) {
		this.inventoryUseCase = inventoryUseCase;
	}

	@GetMapping("/{productId}/available")
	public boolean available(@PathVariable Long productId) {
		return inventoryUseCase.available(productId);
	}

	@PostMapping("/deduct")
	public boolean deduct(@RequestParam Long productId, @RequestParam Integer quantity) {
		return inventoryUseCase.deductStock(productId, quantity);
	}

	@PostMapping("/warehouses")
	public Long createWarehouse(@RequestBody CreateWarehouseRequest request) {
		return inventoryUseCase.createWarehouse(request);
	}

	@GetMapping("/warehouses")
	public List<Warehouse> listWarehouses() {
		return inventoryUseCase.listWarehouses();
	}

	@PostMapping("/bins")
	public Long createBin(@RequestBody CreateBinRequest request) {
		return inventoryUseCase.createBin(request);
	}

	@GetMapping("/warehouses/{warehouseId}/bins")
	public List<Bin> listBins(@PathVariable Long warehouseId) {
		return inventoryUseCase.listBins(warehouseId);
	}

	@PostMapping("/inbound")
	public void inbound(@RequestBody InboundRequest request) {
		inventoryUseCase.inbound(request);
	}

	@PostMapping("/outbound")
	public void outbound(@RequestBody OutboundRequest request) {
		inventoryUseCase.outbound(request);
	}

	@PostMapping("/adjust")
	public void adjust(@RequestBody AdjustRequest request) {
		inventoryUseCase.adjust(request);
	}

	@PostMapping("/adjust-locked")
	public void adjustLocked(@RequestBody AdjustLockedRequest request) {
		inventoryUseCase.adjustLocked(request);
	}

	@PostMapping("/lock")
	public List<Long> lock(@RequestBody LockRequest request) {
		return inventoryUseCase.lock(request);
	}

	@PostMapping("/unlock")
	public void unlock(@RequestBody UnlockRequest request) {
		inventoryUseCase.unlock(request);
	}

	@GetMapping("/balances/{warehouseId}/{skuId}")
	public Balance balance(@PathVariable Long warehouseId, @PathVariable Long skuId) {
		return inventoryUseCase.getBalance(skuId, warehouseId).orElse(null);
	}

	@GetMapping("/bin-balances/{binId}/{skuId}")
	public BinBalance binBalance(@PathVariable Long binId, @PathVariable Long skuId) {
		return inventoryUseCase.getBinBalance(skuId, binId).orElse(null);
	}

	@PostMapping("/receipts")
	public InventoryReceipt createReceipt(@RequestBody CreateReceiptRequest request) {
		return inventoryUseCase.createReceipt(request);
	}

	@PostMapping("/receipts/transit")
	public InventoryReceipt transitReceipt(@RequestBody TransitReceiptRequest request) {
		return inventoryUseCase.transitReceipt(request);
	}

	@GetMapping("/receipts/{receiptId}")
	public InventoryReceipt getReceipt(@PathVariable Long receiptId) {
		return inventoryUseCase.getReceipt(receiptId).orElse(null);
	}

	@GetMapping("/receipts")
	public PageResponse<InventoryReceipt> listReceipts(
			@RequestParam(required = false) String typeKey,
			@RequestParam(defaultValue = "1") int pageNumber,
			@RequestParam(defaultValue = "20") int pageSize
	) {
		return inventoryUseCase.listReceipts(typeKey, pageNumber, pageSize);
	}

}

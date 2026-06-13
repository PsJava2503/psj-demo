package com.commerce.inventory.application.port;

import com.commerce.inventory.domain.model.InventoryModels.AdjustLockedRequest;
import com.commerce.inventory.domain.model.InventoryModels.AdjustRequest;
import com.commerce.inventory.domain.model.InventoryModels.Balance;
import com.commerce.inventory.domain.model.InventoryModels.Bin;
import com.commerce.inventory.domain.model.InventoryModels.BinBalance;
import com.commerce.inventory.domain.model.InventoryModels.CreateBinRequest;
import com.commerce.inventory.domain.model.InventoryModels.CreateReceiptRequest;
import com.commerce.inventory.domain.model.InventoryModels.CreateWarehouseRequest;
import com.commerce.inventory.domain.model.InventoryModels.InventoryReceipt;
import com.commerce.inventory.domain.model.InventoryModels.InboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.LockRequest;
import com.commerce.inventory.domain.model.InventoryModels.OutboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.PageResponse;
import com.commerce.inventory.domain.model.InventoryModels.TransitReceiptRequest;
import com.commerce.inventory.domain.model.InventoryModels.UnlockRequest;
import com.commerce.inventory.domain.model.InventoryModels.Warehouse;
import java.util.List;
import java.util.Optional;

public interface InventoryUseCase {

	boolean deductStock(Long productId, Integer quantity);

	boolean available(Long productId);

	List<Long> reserveStock(Long skuId, Integer quantity, Long orderId);

	void releaseStock(List<Long> reservationIds, Long orderId);

	void confirmStock(List<Long> reservationIds, Long orderId);

	void inbound(InboundRequest request);

	void outbound(OutboundRequest request);

	void adjust(AdjustRequest request);

	void adjustLocked(AdjustLockedRequest request);

	List<Long> lock(LockRequest request);

	void unlock(UnlockRequest request);

	Optional<Balance> getBalance(Long skuId, Long warehouseId);

	Optional<BinBalance> getBinBalance(Long skuId, Long binId);

	Long createWarehouse(CreateWarehouseRequest request);

	Long createBin(CreateBinRequest request);

	List<Warehouse> listWarehouses();

	List<Bin> listBins(Long warehouseId);

	InventoryReceipt createReceipt(CreateReceiptRequest request);

	InventoryReceipt transitReceipt(TransitReceiptRequest request);

	Optional<InventoryReceipt> getReceipt(Long receiptId);

	PageResponse<InventoryReceipt> listReceipts(String typeKey, int pageNumber, int pageSize);

}

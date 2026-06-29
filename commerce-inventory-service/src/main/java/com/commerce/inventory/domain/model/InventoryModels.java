package com.commerce.inventory.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public final class InventoryModels {

	private InventoryModels() {
	}

	public enum BinType {
		Available,
		Defective,
		Inspection;

		public Quantity inboundDelta(long quantity) {
			return switch (this) {
				case Available -> Quantity.availableOnly(quantity);
				case Defective -> new Quantity(0, 0, quantity, 0, 0, 0);
				case Inspection -> new Quantity(0, 0, 0, 0, quantity, 0);
			};
		}

		public Quantity outboundDelta(long quantity) {
			return outboundDelta(quantity, true);
		}

		public Quantity outboundDelta(long quantity, boolean locked) {
			return switch (this) {
				case Available -> locked ? new Quantity(0, -quantity, 0, 0, 0, 0)
						: new Quantity(-quantity, 0, 0, 0, 0, 0);
				case Defective -> locked ? new Quantity(0, 0, 0, -quantity, 0, 0)
						: new Quantity(0, 0, -quantity, 0, 0, 0);
				case Inspection -> new Quantity(0, 0, 0, 0, -quantity, 0);
			};
		}

		public Quantity lockDelta(long quantity) {
			return switch (this) {
				case Available -> new Quantity(-quantity, quantity, 0, 0, 0, 0);
				case Defective -> new Quantity(0, 0, -quantity, quantity, 0, 0);
				case Inspection -> Quantity.zero();
			};
		}
	}

	public enum TransactionType {
		InOther,
		InPurchaseRaw,
		InPurchaseOutsource,
		InPurchaseOther,
		InPurchaseInspection,
		InProductionInspection,
		InTransfer,
		InStockProfit,
		InRepairReturn,
		InProduction,
		OutSalesDelivery,
		OutStockLoss,
		OutTransfer,
		OutOther,
		OutReturn,
		OutProduction,
		StockAdjustment,
		LocationTransfer
	}

	public enum WarehouseLedgerType {
		InOther,
		InPurchaseRaw,
		InPurchaseOutsource,
		InPurchaseOther,
		InPurchaseInspection,
		InProductionInspection,
		InTransfer,
		InStockProfit,
		InRepairReturn,
		InProduction,
		OutSalesDelivery,
		OutStockLoss,
		OutTransfer,
		OutOther,
		OutReturn,
		OutProduction,
		StockAdjustment
	}

	public enum BinLedgerType {
		InOther,
		InPurchaseRaw,
		InPurchaseOutsource,
		InPurchaseOther,
		InPurchaseInspection,
		InProductionInspection,
		InTransfer,
		InStockProfit,
		InRepairReturn,
		InProduction,
		OutSalesDelivery,
		OutStockLoss,
		OutTransfer,
		OutOther,
		OutReturn,
		OutProduction,
		RelocatePutaway,
		RelocatePicking
	}

	public enum ReceiptType {
		INBOUND,
		OUTBOUND,
		TRANSFER,
		LOCATION_TRANSFER
	}

	public enum ReceiptEvent {
		Reserve,
		Submit,
		ConfirmInbound,
		ConfirmOutbound,
		ConfirmTransfer,
		ConfirmTransferInbound,
		ConfirmLocationTransfer,
		Rollback,
		Cancel
	}

	public enum NumberType {
		INBOUND("IB"),
		OUTBOUND("OB"),
		TRANSFER("TF"),
		LOCATION_TRANSFER("LT");

		private final String prefix;

		NumberType(String prefix) {
			this.prefix = prefix;
		}

		public String prefix() {
			return prefix;
		}
	}

	public record Quantity(
			long available,
			long availableLock,
			long defective,
			long defectiveLock,
			long toInspection,
			long transferInTransit
	) {
		public static Quantity zero() {
			return new Quantity(0, 0, 0, 0, 0, 0);
		}

		public static Quantity availableOnly(long available) {
			return new Quantity(available, 0, 0, 0, 0, 0);
		}

		public Quantity combine(Quantity other) {
			return new Quantity(
					available + other.available,
					availableLock + other.availableLock,
					defective + other.defective,
					defectiveLock + other.defectiveLock,
					toInspection + other.toInspection,
					transferInTransit + other.transferInTransit
			);
		}

		public boolean hasChange() {
			return available != 0 || availableLock != 0 || defective != 0 || defectiveLock != 0
					|| toInspection != 0 || transferInTransit != 0;
		}

		public boolean isNonNegative() {
			return available >= 0 && availableLock >= 0 && defective >= 0 && defectiveLock >= 0
					&& toInspection >= 0 && transferInTransit >= 0;
		}
	}

	public record BinQuantity(long quantity, long lockQty) {
		public static BinQuantity zero() {
			return new BinQuantity(0, 0);
		}

		public static BinQuantity quantityOnly(long quantity) {
			return new BinQuantity(quantity, 0);
		}

		public static BinQuantity lockOnly(long lockQty) {
			return new BinQuantity(0, lockQty);
		}

		public BinQuantity combine(BinQuantity other) {
			return new BinQuantity(quantity + other.quantity, lockQty + other.lockQty);
		}
	}

	public record Warehouse(
			Long id,
			String name,
			String address,
			Long manager,
			String phone,
			String description,
			boolean disable,
			ZonedDateTime createTime
	) {}

	public record Bin(
			Long id,
			Long warehouseId,
			String name,
			String zone,
			String row,
			String col,
			String level,
			String slot,
			BinType binType,
			String remark,
			boolean disable,
			ZonedDateTime createTime,
			ZonedDateTime disableTime
	) {}

	public record Balance(
			Long id,
			Long skuId,
			Long warehouseId,
			long availableQty,
			long availableLockQty,
			long defectiveQty,
			long defectiveLockQty,
			long toInspectionQty,
			long transferInTransit,
			ZonedDateTime createdTime,
			ZonedDateTime updateTime
	) {
		public Quantity quantity() {
			return new Quantity(availableQty, availableLockQty, defectiveQty, defectiveLockQty, toInspectionQty,
					transferInTransit);
		}
	}

	public record BinBalance(
			Long id,
			Long skuId,
			Long binId,
			long quantity,
			long lockQty,
			ZonedDateTime createdTime,
			ZonedDateTime updateTime
	) {}

	public record Reservation(
			Long id,
			Long skuId,
			Long warehouseId,
			Long binId,
			Long parentReservationId,
			Long transactionId,
			long reserved,
			long consumed,
			long released,
			long remaining,
			ZonedDateTime availableFrom,
			ZonedDateTime availableTo,
			ZonedDateTime createdTime,
			ZonedDateTime updateTime,
			ZonedDateTime cancelledTime,
			ZonedDateTime releasedTime,
			String remark
	) {}

	public record WarehouseLedger(
			Long id,
			Long skuId,
			Long warehouseId,
			WarehouseLedgerType warehouseLedgerType,
			long availableDelta,
			long availableLockDelta,
			long defectiveDelta,
			long defectiveLockDelta,
			long toInspectionDelta,
			long transferInTransitDelta,
			long availableBalance,
			long availableLockBalance,
			long defectiveBalance,
			long defectiveLockBalance,
			long toInspectionBalance,
			long transferInTransitBalance,
			Long transactionId,
			ZonedDateTime createdTime,
			String remark
	) {}

	public record BinLedger(
			Long id,
			Long reservationId,
			Long skuId,
			Long binId,
			BinLedgerType binLedgerType,
			long delta,
			long lockDelta,
			long balance,
			long lockBalance,
			Long transactionId,
			ZonedDateTime createdTime,
			String remark
	) {}

	public record InBounding(Long skuId, Long binId, long quantity) {}

	public record OutBounding(Long skuId, Long binId, long quantity, Long reservationId, Long toWarehouseId) {}

	public record Adjusting(Long skuId, Long fromBinId, Long toBinId, long quantity) {}

	public record Locking(Long skuId, Long binId, Long warehouseId, long quantity) {}

	public record InboundRequest(
			TransactionType transactionType,
			List<InBounding> items,
			Long sourceOrderId,
			Long operatorId,
			String remark,
			List<Long> linkedOrderIds
	) {}

	public record OutboundRequest(
			TransactionType transactionType,
			List<OutBounding> items,
			Long sourceOrderId,
			Long operatorId,
			String remark,
			List<Long> linkedOrderIds
	) {}

	public record AdjustRequest(
			TransactionType transactionType,
			List<Adjusting> items,
			Long sourceOrderId,
			Long operatorId,
			String remark,
			List<Long> linkedOrderIds
	) {}

	public record AdjustLockedRequest(
			TransactionType transactionType,
			List<OutBounding> items,
			Map<Long, Long> targetBinByReservationId,
			BinLedgerType sourceBinLedgerType,
			BinLedgerType targetBinLedgerType,
			Long sourceOrderId,
			Long operatorId,
			String remark,
			List<Long> linkedOrderIds
	) {}

	public record LockRequest(
			TransactionType transactionType,
			List<Locking> items,
			Long sourceOrderId,
			Long operatorId,
			String remark,
			String ledgerRemark,
			List<Long> linkedOrderIds,
			ZonedDateTime availableTo
	) {
		public LockRequest(
				TransactionType transactionType,
				List<Locking> items,
				Long sourceOrderId,
				Long operatorId,
				String remark,
				String ledgerRemark,
				List<Long> linkedOrderIds
		) {
			this(transactionType, items, sourceOrderId, operatorId, remark, ledgerRemark, linkedOrderIds, null);
		}
	}

	public record UnlockRequest(
			TransactionType transactionType,
			List<OutBounding> items,
			Long sourceOrderId,
			Long operatorId,
			String remark,
			String ledgerRemark,
			boolean cancelReservation,
			List<Long> linkedOrderIds
	) {}

	public record InboundItem(
			Long skuId,
			int availableQty,
			Long availableBinId,
			int inspectionQty,
			Long inspectionBinId,
			int defectiveQty,
			Long defectiveBinId,
			String remark
	) {}

	public record InboundPayload(
			TransactionType inBoundingType,
			Long warehouseId,
			Long supplierId,
			Long purchaserId,
			List<InboundItem> items,
			String remark,
			Long linkedTransferReceiptId,
			Long operatorId,
			ZonedDateTime submitTime
	) {}

	public record OutboundItem(
			Long skuId,
			int availableQty,
			int inspectionQty,
			int defectiveQty,
			List<Long> reservationIds,
			List<OutboundBinAllocation> binAllocations,
			String remark
	) {}

	public record OutboundBinAllocation(Long binId, int quantity) {}

	public record OutboundPayload(
			TransactionType outBoundingType,
			Long warehouseId,
			List<OutboundItem> items,
			Long linkedTransferReceiptId,
			String remark,
			Long operatorId,
			ZonedDateTime submitTime
	) {}

	public record TransferItem(
			Long skuId,
			int availableQty,
			int inspectionQty,
			int defectiveQty,
			Long availableTargetBinId,
			Long inspectionTargetBinId,
			Long defectiveTargetBinId,
			String remark
	) {}

	public record TransferPayload(
			List<TransferItem> items,
			Long sourceWarehouseId,
			Long targetWarehouseId,
			String remark,
			Long linkedOutboundReceiptId,
			Long linkedInboundReceiptId,
			Long operatorId,
			ZonedDateTime submitTime,
			ZonedDateTime outboundTime,
			ZonedDateTime inboundTime
	) {}

	public record LocationTransferPayload(
			Long skuId,
			int quantity,
			Long warehouseId,
			Long sourceBinId,
			Long targetBinId,
			Long reservationId,
			Long operatorId,
			ZonedDateTime submitTime,
			ZonedDateTime locationTransferTime
	) {}

	public record InventoryReceipt(
			Long id,
			String number,
			ReceiptType typeKey,
			String state,
			JsonNode payload,
			Long createdBy,
			ZonedDateTime createTime,
			Long lastOperator,
			ZonedDateTime updateTime,
			ZonedDateTime completeTime
	) {}

	public record CreateReceiptRequest(
			ReceiptType typeKey,
			JsonNode payload,
			Long operatorId,
			String number,
			String initialState
	) {}

	public record CreateWarehouseRequest(
			String name,
			String address,
			Long manager,
			String phone,
			String description
	) {}

	public record CreateBinRequest(
			Long warehouseId,
			String name,
			String zone,
			String row,
			String col,
			String level,
			String slot,
			BinType binType,
			String remark
	) {}

	public record TransitReceiptRequest(
			Long receiptId,
			ReceiptEvent event,
			JsonNode payload,
			Long operatorId
	) {}

	public record PageResponse<T>(List<T> data, int pageNumber, int pageSize, long total) {}
}

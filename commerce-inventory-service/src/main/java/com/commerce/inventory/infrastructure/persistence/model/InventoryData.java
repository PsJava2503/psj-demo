package com.commerce.inventory.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public final class InventoryData {

	private InventoryData() {
	}

	public record WarehouseData(
			Long id,
			String name,
			String address,
			Long manager,
			String phone,
			String description,
			Boolean disable,
			ZonedDateTime createTime
	) {}

	public record BinData(
			Long id,
			Long warehouseId,
			String name,
			String zone,
			String row,
			String col,
			String level,
			String slot,
			String binType,
			String remark,
			Boolean disable,
			ZonedDateTime createTime,
			ZonedDateTime disableTime
	) {}

	public record BalanceData(
			Long id,
			Long skuId,
			Long warehouseId,
			Long availableQty,
			Long availableLockQty,
			Long defectiveQty,
			Long defectiveLockQty,
			Long toInspectionQty,
			Long transferInTransit,
			ZonedDateTime createdTime,
			ZonedDateTime updateTime
	) {}

	public record BinBalanceData(
			Long id,
			Long skuId,
			Long binId,
			Long quantity,
			Long lockQty,
			ZonedDateTime createdTime,
			ZonedDateTime updateTime
	) {}

	public record ReservationData(
			Long id,
			Long skuId,
			Long warehouseId,
			Long binId,
			Long parentReservationId,
			Long transactionId,
			Long reserved,
			Long consumed,
			Long released,
			Long remaining,
			ZonedDateTime availableFrom,
			ZonedDateTime availableTo,
			ZonedDateTime createdTime,
			ZonedDateTime updateTime,
			ZonedDateTime cancelledTime,
			ZonedDateTime releasedTime,
			String remark
	) {}

	public record ReceiptRowData(
			Long id,
			String number,
			String typeKey,
			Long creator,
			ZonedDateTime createTime,
			String state,
			String payload,
			Long operator,
			ZonedDateTime recordTime
	) {}
}

package com.commerce.inventory.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public final class InventoryData {

	private InventoryData() {
	}

	public static class WarehouseData {

		private Long id;
		private String name;
		private String address;
		private Long manager;
		private String phone;
		private String description;
		private Boolean disable;
		private ZonedDateTime createTime;

		public WarehouseData() {
		}

		public WarehouseData(Long id, String name, String address, Long manager, String phone, String description, Boolean disable, ZonedDateTime createTime) {
			this.id = id;
			this.name = name;
			this.address = address;
			this.manager = manager;
			this.phone = phone;
			this.description = description;
			this.disable = disable;
			this.createTime = createTime;
		}

		public Long id() {
			return id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String name() {
			return name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String address() {
			return address;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Long manager() {
			return manager;
		}

		public Long getManager() {
			return manager;
		}

		public void setManager(Long manager) {
			this.manager = manager;
		}

		public String phone() {
			return phone;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String description() {
			return description;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Boolean disable() {
			return disable;
		}

		public Boolean getDisable() {
			return disable;
		}

		public void setDisable(Boolean disable) {
			this.disable = disable;
		}

		public ZonedDateTime createTime() {
			return createTime;
		}

		public ZonedDateTime getCreateTime() {
			return createTime;
		}

		public void setCreateTime(ZonedDateTime createTime) {
			this.createTime = createTime;
		}

	}

	public static class BinData {

		private Long id;
		private Long warehouseId;
		private String name;
		private String zone;
		private String row;
		private String col;
		private String level;
		private String slot;
		private String binType;
		private String remark;
		private Boolean disable;
		private ZonedDateTime createTime;
		private ZonedDateTime disableTime;

		public BinData() {
		}

		public BinData(Long id, Long warehouseId, String name, String zone, String row, String col, String level, String slot, String binType, String remark, Boolean disable, ZonedDateTime createTime, ZonedDateTime disableTime) {
			this.id = id;
			this.warehouseId = warehouseId;
			this.name = name;
			this.zone = zone;
			this.row = row;
			this.col = col;
			this.level = level;
			this.slot = slot;
			this.binType = binType;
			this.remark = remark;
			this.disable = disable;
			this.createTime = createTime;
			this.disableTime = disableTime;
		}

		public Long id() {
			return id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long warehouseId() {
			return warehouseId;
		}

		public Long getWarehouseId() {
			return warehouseId;
		}

		public void setWarehouseId(Long warehouseId) {
			this.warehouseId = warehouseId;
		}

		public String name() {
			return name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String zone() {
			return zone;
		}

		public String getZone() {
			return zone;
		}

		public void setZone(String zone) {
			this.zone = zone;
		}

		public String row() {
			return row;
		}

		public String getRow() {
			return row;
		}

		public void setRow(String row) {
			this.row = row;
		}

		public String col() {
			return col;
		}

		public String getCol() {
			return col;
		}

		public void setCol(String col) {
			this.col = col;
		}

		public String level() {
			return level;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String slot() {
			return slot;
		}

		public String getSlot() {
			return slot;
		}

		public void setSlot(String slot) {
			this.slot = slot;
		}

		public String binType() {
			return binType;
		}

		public String getBinType() {
			return binType;
		}

		public void setBinType(String binType) {
			this.binType = binType;
		}

		public String remark() {
			return remark;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public Boolean disable() {
			return disable;
		}

		public Boolean getDisable() {
			return disable;
		}

		public void setDisable(Boolean disable) {
			this.disable = disable;
		}

		public ZonedDateTime createTime() {
			return createTime;
		}

		public ZonedDateTime getCreateTime() {
			return createTime;
		}

		public void setCreateTime(ZonedDateTime createTime) {
			this.createTime = createTime;
		}

		public ZonedDateTime disableTime() {
			return disableTime;
		}

		public ZonedDateTime getDisableTime() {
			return disableTime;
		}

		public void setDisableTime(ZonedDateTime disableTime) {
			this.disableTime = disableTime;
		}

	}

	public static class BalanceData {

		private Long id;
		private Long skuId;
		private Long warehouseId;
		private Long availableQty;
		private Long availableLockQty;
		private Long defectiveQty;
		private Long defectiveLockQty;
		private Long toInspectionQty;
		private Long transferInTransit;
		private ZonedDateTime createdTime;
		private ZonedDateTime updateTime;

		public BalanceData() {
		}

		public BalanceData(Long id, Long skuId, Long warehouseId, Long availableQty, Long availableLockQty, Long defectiveQty, Long defectiveLockQty, Long toInspectionQty, Long transferInTransit, ZonedDateTime createdTime, ZonedDateTime updateTime) {
			this.id = id;
			this.skuId = skuId;
			this.warehouseId = warehouseId;
			this.availableQty = availableQty;
			this.availableLockQty = availableLockQty;
			this.defectiveQty = defectiveQty;
			this.defectiveLockQty = defectiveLockQty;
			this.toInspectionQty = toInspectionQty;
			this.transferInTransit = transferInTransit;
			this.createdTime = createdTime;
			this.updateTime = updateTime;
		}

		public Long id() {
			return id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long skuId() {
			return skuId;
		}

		public Long getSkuId() {
			return skuId;
		}

		public void setSkuId(Long skuId) {
			this.skuId = skuId;
		}

		public Long warehouseId() {
			return warehouseId;
		}

		public Long getWarehouseId() {
			return warehouseId;
		}

		public void setWarehouseId(Long warehouseId) {
			this.warehouseId = warehouseId;
		}

		public Long availableQty() {
			return availableQty;
		}

		public Long getAvailableQty() {
			return availableQty;
		}

		public void setAvailableQty(Long availableQty) {
			this.availableQty = availableQty;
		}

		public Long availableLockQty() {
			return availableLockQty;
		}

		public Long getAvailableLockQty() {
			return availableLockQty;
		}

		public void setAvailableLockQty(Long availableLockQty) {
			this.availableLockQty = availableLockQty;
		}

		public Long defectiveQty() {
			return defectiveQty;
		}

		public Long getDefectiveQty() {
			return defectiveQty;
		}

		public void setDefectiveQty(Long defectiveQty) {
			this.defectiveQty = defectiveQty;
		}

		public Long defectiveLockQty() {
			return defectiveLockQty;
		}

		public Long getDefectiveLockQty() {
			return defectiveLockQty;
		}

		public void setDefectiveLockQty(Long defectiveLockQty) {
			this.defectiveLockQty = defectiveLockQty;
		}

		public Long toInspectionQty() {
			return toInspectionQty;
		}

		public Long getToInspectionQty() {
			return toInspectionQty;
		}

		public void setToInspectionQty(Long toInspectionQty) {
			this.toInspectionQty = toInspectionQty;
		}

		public Long transferInTransit() {
			return transferInTransit;
		}

		public Long getTransferInTransit() {
			return transferInTransit;
		}

		public void setTransferInTransit(Long transferInTransit) {
			this.transferInTransit = transferInTransit;
		}

		public ZonedDateTime createdTime() {
			return createdTime;
		}

		public ZonedDateTime getCreatedTime() {
			return createdTime;
		}

		public void setCreatedTime(ZonedDateTime createdTime) {
			this.createdTime = createdTime;
		}

		public ZonedDateTime updateTime() {
			return updateTime;
		}

		public ZonedDateTime getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(ZonedDateTime updateTime) {
			this.updateTime = updateTime;
		}

	}

	public static class BinBalanceData {

		private Long id;
		private Long skuId;
		private Long binId;
		private Long quantity;
		private Long lockQty;
		private ZonedDateTime createdTime;
		private ZonedDateTime updateTime;

		public BinBalanceData() {
		}

		public BinBalanceData(Long id, Long skuId, Long binId, Long quantity, Long lockQty, ZonedDateTime createdTime, ZonedDateTime updateTime) {
			this.id = id;
			this.skuId = skuId;
			this.binId = binId;
			this.quantity = quantity;
			this.lockQty = lockQty;
			this.createdTime = createdTime;
			this.updateTime = updateTime;
		}

		public Long id() {
			return id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long skuId() {
			return skuId;
		}

		public Long getSkuId() {
			return skuId;
		}

		public void setSkuId(Long skuId) {
			this.skuId = skuId;
		}

		public Long binId() {
			return binId;
		}

		public Long getBinId() {
			return binId;
		}

		public void setBinId(Long binId) {
			this.binId = binId;
		}

		public Long quantity() {
			return quantity;
		}

		public Long getQuantity() {
			return quantity;
		}

		public void setQuantity(Long quantity) {
			this.quantity = quantity;
		}

		public Long lockQty() {
			return lockQty;
		}

		public Long getLockQty() {
			return lockQty;
		}

		public void setLockQty(Long lockQty) {
			this.lockQty = lockQty;
		}

		public ZonedDateTime createdTime() {
			return createdTime;
		}

		public ZonedDateTime getCreatedTime() {
			return createdTime;
		}

		public void setCreatedTime(ZonedDateTime createdTime) {
			this.createdTime = createdTime;
		}

		public ZonedDateTime updateTime() {
			return updateTime;
		}

		public ZonedDateTime getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(ZonedDateTime updateTime) {
			this.updateTime = updateTime;
		}

	}

	public static class ReservationData {

		private Long id;
		private Long skuId;
		private Long warehouseId;
		private Long binId;
		private Long parentReservationId;
		private Long transactionId;
		private Long reserved;
		private Long consumed;
		private Long released;
		private Long remaining;
		private ZonedDateTime availableFrom;
		private ZonedDateTime availableTo;
		private ZonedDateTime createdTime;
		private ZonedDateTime updateTime;
		private ZonedDateTime cancelledTime;
		private ZonedDateTime releasedTime;
		private String remark;

		public ReservationData() {
		}

		public ReservationData(Long id, Long skuId, Long warehouseId, Long binId, Long parentReservationId, Long transactionId, Long reserved, Long consumed, Long released, Long remaining, ZonedDateTime availableFrom, ZonedDateTime availableTo, ZonedDateTime createdTime, ZonedDateTime updateTime, ZonedDateTime cancelledTime, ZonedDateTime releasedTime, String remark) {
			this.id = id;
			this.skuId = skuId;
			this.warehouseId = warehouseId;
			this.binId = binId;
			this.parentReservationId = parentReservationId;
			this.transactionId = transactionId;
			this.reserved = reserved;
			this.consumed = consumed;
			this.released = released;
			this.remaining = remaining;
			this.availableFrom = availableFrom;
			this.availableTo = availableTo;
			this.createdTime = createdTime;
			this.updateTime = updateTime;
			this.cancelledTime = cancelledTime;
			this.releasedTime = releasedTime;
			this.remark = remark;
		}

		public Long id() {
			return id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long skuId() {
			return skuId;
		}

		public Long getSkuId() {
			return skuId;
		}

		public void setSkuId(Long skuId) {
			this.skuId = skuId;
		}

		public Long warehouseId() {
			return warehouseId;
		}

		public Long getWarehouseId() {
			return warehouseId;
		}

		public void setWarehouseId(Long warehouseId) {
			this.warehouseId = warehouseId;
		}

		public Long binId() {
			return binId;
		}

		public Long getBinId() {
			return binId;
		}

		public void setBinId(Long binId) {
			this.binId = binId;
		}

		public Long parentReservationId() {
			return parentReservationId;
		}

		public Long getParentReservationId() {
			return parentReservationId;
		}

		public void setParentReservationId(Long parentReservationId) {
			this.parentReservationId = parentReservationId;
		}

		public Long transactionId() {
			return transactionId;
		}

		public Long getTransactionId() {
			return transactionId;
		}

		public void setTransactionId(Long transactionId) {
			this.transactionId = transactionId;
		}

		public Long reserved() {
			return reserved;
		}

		public Long getReserved() {
			return reserved;
		}

		public void setReserved(Long reserved) {
			this.reserved = reserved;
		}

		public Long consumed() {
			return consumed;
		}

		public Long getConsumed() {
			return consumed;
		}

		public void setConsumed(Long consumed) {
			this.consumed = consumed;
		}

		public Long released() {
			return released;
		}

		public Long getReleased() {
			return released;
		}

		public void setReleased(Long released) {
			this.released = released;
		}

		public Long remaining() {
			return remaining;
		}

		public Long getRemaining() {
			return remaining;
		}

		public void setRemaining(Long remaining) {
			this.remaining = remaining;
		}

		public ZonedDateTime availableFrom() {
			return availableFrom;
		}

		public ZonedDateTime getAvailableFrom() {
			return availableFrom;
		}

		public void setAvailableFrom(ZonedDateTime availableFrom) {
			this.availableFrom = availableFrom;
		}

		public ZonedDateTime availableTo() {
			return availableTo;
		}

		public ZonedDateTime getAvailableTo() {
			return availableTo;
		}

		public void setAvailableTo(ZonedDateTime availableTo) {
			this.availableTo = availableTo;
		}

		public ZonedDateTime createdTime() {
			return createdTime;
		}

		public ZonedDateTime getCreatedTime() {
			return createdTime;
		}

		public void setCreatedTime(ZonedDateTime createdTime) {
			this.createdTime = createdTime;
		}

		public ZonedDateTime updateTime() {
			return updateTime;
		}

		public ZonedDateTime getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(ZonedDateTime updateTime) {
			this.updateTime = updateTime;
		}

		public ZonedDateTime cancelledTime() {
			return cancelledTime;
		}

		public ZonedDateTime getCancelledTime() {
			return cancelledTime;
		}

		public void setCancelledTime(ZonedDateTime cancelledTime) {
			this.cancelledTime = cancelledTime;
		}

		public ZonedDateTime releasedTime() {
			return releasedTime;
		}

		public ZonedDateTime getReleasedTime() {
			return releasedTime;
		}

		public void setReleasedTime(ZonedDateTime releasedTime) {
			this.releasedTime = releasedTime;
		}

		public String remark() {
			return remark;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

	}

	public static class ReceiptRowData {

		private Long id;
		private String number;
		private String typeKey;
		private Long creator;
		private ZonedDateTime createTime;
		private String state;
		private String payload;
		private Long operator;
		private ZonedDateTime recordTime;

		public ReceiptRowData() {
		}

		public ReceiptRowData(Long id, String number, String typeKey, Long creator, ZonedDateTime createTime, String state, String payload, Long operator, ZonedDateTime recordTime) {
			this.id = id;
			this.number = number;
			this.typeKey = typeKey;
			this.creator = creator;
			this.createTime = createTime;
			this.state = state;
			this.payload = payload;
			this.operator = operator;
			this.recordTime = recordTime;
		}

		public Long id() {
			return id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String number() {
			return number;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String typeKey() {
			return typeKey;
		}

		public String getTypeKey() {
			return typeKey;
		}

		public void setTypeKey(String typeKey) {
			this.typeKey = typeKey;
		}

		public Long creator() {
			return creator;
		}

		public Long getCreator() {
			return creator;
		}

		public void setCreator(Long creator) {
			this.creator = creator;
		}

		public ZonedDateTime createTime() {
			return createTime;
		}

		public ZonedDateTime getCreateTime() {
			return createTime;
		}

		public void setCreateTime(ZonedDateTime createTime) {
			this.createTime = createTime;
		}

		public String state() {
			return state;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String payload() {
			return payload;
		}

		public String getPayload() {
			return payload;
		}

		public void setPayload(String payload) {
			this.payload = payload;
		}

		public Long operator() {
			return operator;
		}

		public Long getOperator() {
			return operator;
		}

		public void setOperator(Long operator) {
			this.operator = operator;
		}

		public ZonedDateTime recordTime() {
			return recordTime;
		}

		public ZonedDateTime getRecordTime() {
			return recordTime;
		}

		public void setRecordTime(ZonedDateTime recordTime) {
			this.recordTime = recordTime;
		}

	}
}

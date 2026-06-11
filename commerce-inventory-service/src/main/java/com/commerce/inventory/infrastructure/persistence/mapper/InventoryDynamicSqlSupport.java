package com.commerce.inventory.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class InventoryDynamicSqlSupport {

	public static final WarehouseTable warehouses = new WarehouseTable();
	public static final BinTable bins = new BinTable();
	public static final BalanceTable balances = new BalanceTable();
	public static final BinBalanceTable binBalances = new BinBalanceTable();
	public static final ReservationTable reservations = new ReservationTable();

	public static final SqlColumn<Long> warehouseId = warehouses.id;
	public static final SqlColumn<String> warehouseName = warehouses.name;
	public static final SqlColumn<String> address = warehouses.address;
	public static final SqlColumn<Long> manager = warehouses.manager;
	public static final SqlColumn<String> phone = warehouses.phone;
	public static final SqlColumn<String> description = warehouses.description;
	public static final SqlColumn<Boolean> warehouseDisable = warehouses.disable;
	public static final SqlColumn<ZonedDateTime> warehouseCreateTime = warehouses.createTime;

	public static final SqlColumn<Long> binId = bins.id;
	public static final SqlColumn<Long> binWarehouseId = bins.warehouseId;
	public static final SqlColumn<String> binName = bins.name;
	public static final SqlColumn<String> zone = bins.zone;
	public static final SqlColumn<String> row = bins.row;
	public static final SqlColumn<String> col = bins.col;
	public static final SqlColumn<String> level = bins.level;
	public static final SqlColumn<String> slot = bins.slot;
	public static final SqlColumn<String> binType = bins.binType;
	public static final SqlColumn<String> binRemark = bins.remark;
	public static final SqlColumn<Boolean> binDisable = bins.disable;
	public static final SqlColumn<ZonedDateTime> binCreateTime = bins.createTime;
	public static final SqlColumn<ZonedDateTime> binDisableTime = bins.disableTime;

	public static final SqlColumn<Long> balanceId = balances.id;
	public static final SqlColumn<Long> balanceSkuId = balances.skuId;
	public static final SqlColumn<Long> balanceWarehouseId = balances.warehouseId;
	public static final SqlColumn<Long> availableQty = balances.availableQty;
	public static final SqlColumn<Long> availableLockQty = balances.availableLockQty;
	public static final SqlColumn<Long> defectiveQty = balances.defectiveQty;
	public static final SqlColumn<Long> defectiveLockQty = balances.defectiveLockQty;
	public static final SqlColumn<Long> toInspectionQty = balances.toInspectionQty;
	public static final SqlColumn<Long> transferInTransit = balances.transferInTransit;
	public static final SqlColumn<ZonedDateTime> balanceCreatedTime = balances.createdTime;
	public static final SqlColumn<ZonedDateTime> balanceUpdateTime = balances.updateTime;

	public static final SqlColumn<Long> binBalanceId = binBalances.id;
	public static final SqlColumn<Long> binBalanceSkuId = binBalances.skuId;
	public static final SqlColumn<Long> binBalanceBinId = binBalances.binId;
	public static final SqlColumn<Long> quantity = binBalances.quantity;
	public static final SqlColumn<Long> lockQty = binBalances.lockQty;
	public static final SqlColumn<ZonedDateTime> binBalanceCreatedTime = binBalances.createdTime;
	public static final SqlColumn<ZonedDateTime> binBalanceUpdateTime = binBalances.updateTime;

	public static final SqlColumn<Long> reservationId = reservations.id;
	public static final SqlColumn<Long> reservationSkuId = reservations.skuId;
	public static final SqlColumn<Long> reservationWarehouseId = reservations.warehouseId;
	public static final SqlColumn<Long> reservationBinId = reservations.binId;
	public static final SqlColumn<Long> parentReservationId = reservations.parentReservationId;
	public static final SqlColumn<Long> reservationTransactionId = reservations.transactionId;
	public static final SqlColumn<Long> reserved = reservations.reserved;
	public static final SqlColumn<Long> consumed = reservations.consumed;
	public static final SqlColumn<Long> released = reservations.released;
	public static final SqlColumn<Long> remaining = reservations.remaining;
	public static final SqlColumn<ZonedDateTime> availableFrom = reservations.availableFrom;
	public static final SqlColumn<ZonedDateTime> availableTo = reservations.availableTo;
	public static final SqlColumn<ZonedDateTime> reservationCreatedTime = reservations.createdTime;
	public static final SqlColumn<ZonedDateTime> reservationUpdateTime = reservations.updateTime;
	public static final SqlColumn<ZonedDateTime> cancelledTime = reservations.cancelledTime;
	public static final SqlColumn<ZonedDateTime> releasedTime = reservations.releasedTime;
	public static final SqlColumn<String> reservationRemark = reservations.remark;

	private InventoryDynamicSqlSupport() {
	}

	public static final class WarehouseTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> name = column("name");
		public final SqlColumn<String> address = column("address");
		public final SqlColumn<Long> manager = column("manager");
		public final SqlColumn<String> phone = column("phone");
		public final SqlColumn<String> description = column("description");
		public final SqlColumn<Boolean> disable = column("disable");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");

		public WarehouseTable() {
			super("inv_warehouses");
		}
	}

	public static final class BinTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> warehouseId = column("warehouse_id");
		public final SqlColumn<String> name = column("name");
		public final SqlColumn<String> zone = column("zone");
		public final SqlColumn<String> row = column("\"row\"");
		public final SqlColumn<String> col = column("col");
		public final SqlColumn<String> level = column("level");
		public final SqlColumn<String> slot = column("slot");
		public final SqlColumn<String> binType = column("bin_type");
		public final SqlColumn<String> remark = column("remark");
		public final SqlColumn<Boolean> disable = column("disable");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> disableTime = column("disable_time");

		public BinTable() {
			super("inv_bins");
		}
	}

	public static final class BalanceTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> skuId = column("sku_id");
		public final SqlColumn<Long> warehouseId = column("warehouse_id");
		public final SqlColumn<Long> availableQty = column("available_qty");
		public final SqlColumn<Long> availableLockQty = column("available_lock_qty");
		public final SqlColumn<Long> defectiveQty = column("defective_qty");
		public final SqlColumn<Long> defectiveLockQty = column("defective_lock_qty");
		public final SqlColumn<Long> toInspectionQty = column("to_inspection_qty");
		public final SqlColumn<Long> transferInTransit = column("transfer_in_transit");
		public final SqlColumn<ZonedDateTime> createdTime = column("created_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public BalanceTable() {
			super("inv_balances");
		}
	}

	public static final class BinBalanceTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> skuId = column("sku_id");
		public final SqlColumn<Long> binId = column("bin_id");
		public final SqlColumn<Long> quantity = column("quantity");
		public final SqlColumn<Long> lockQty = column("lock_qty");
		public final SqlColumn<ZonedDateTime> createdTime = column("created_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public BinBalanceTable() {
			super("inv_bin_balances");
		}
	}

	public static final class ReservationTable extends SqlTable {
		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> skuId = column("sku_id");
		public final SqlColumn<Long> warehouseId = column("warehouse_id");
		public final SqlColumn<Long> binId = column("bin_id");
		public final SqlColumn<Long> parentReservationId = column("parent_reservation_id");
		public final SqlColumn<Long> transactionId = column("transaction_id");
		public final SqlColumn<Long> reserved = column("reserved");
		public final SqlColumn<Long> consumed = column("consumed");
		public final SqlColumn<Long> released = column("released");
		public final SqlColumn<Long> remaining = column("remaining");
		public final SqlColumn<ZonedDateTime> availableFrom = column("available_from");
		public final SqlColumn<ZonedDateTime> availableTo = column("available_to");
		public final SqlColumn<ZonedDateTime> createdTime = column("created_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");
		public final SqlColumn<ZonedDateTime> cancelledTime = column("cancelled_time");
		public final SqlColumn<ZonedDateTime> releasedTime = column("released_time");
		public final SqlColumn<String> remark = column("remark");

		public ReservationTable() {
			super("inv_reservations");
		}
	}
}

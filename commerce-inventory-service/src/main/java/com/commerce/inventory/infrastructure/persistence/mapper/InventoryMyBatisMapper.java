package com.commerce.inventory.infrastructure.persistence.mapper;

import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.address;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.availableFrom;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.availableLockQty;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.availableQty;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.availableTo;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.balanceCreatedTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.balanceId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.balanceSkuId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.balanceUpdateTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.balanceWarehouseId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.balances;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binBalanceBinId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binBalanceCreatedTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binBalanceId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binBalanceSkuId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binBalanceUpdateTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binBalances;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binCreateTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binDisable;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binDisableTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binName;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binRemark;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binType;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.binWarehouseId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.bins;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.cancelledTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.col;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.consumed;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.defectiveLockQty;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.defectiveQty;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.description;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.level;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.lockQty;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.manager;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.parentReservationId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.phone;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.quantity;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.released;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.releasedTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.remaining;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservationBinId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservationCreatedTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservationId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservationRemark;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservationSkuId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservationTransactionId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservationUpdateTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservationWarehouseId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reservations;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.reserved;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.row;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.slot;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.toInspectionQty;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.transferInTransit;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.warehouseCreateTime;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.warehouseDisable;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.warehouseId;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.warehouseName;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.warehouses;
import static com.commerce.inventory.infrastructure.persistence.mapper.InventoryDynamicSqlSupport.zone;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import com.commerce.inventory.domain.model.BalanceQueryOptions;
import com.commerce.inventory.domain.model.BinBalanceQueryOptions;
import com.commerce.inventory.domain.model.BinQueryOptions;
import com.commerce.inventory.domain.model.ReservationQueryOptions;
import com.commerce.inventory.domain.model.WarehouseQueryOptions;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.BalanceData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.BinBalanceData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.BinData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.ReceiptRowData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.ReservationData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.WarehouseData;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface InventoryMyBatisMapper {

	BasicColumn[] warehouseColumns = BasicColumn.columnList(
			warehouseId, warehouseName, address, manager, phone, description, warehouseDisable, warehouseCreateTime
	);
	BasicColumn[] binColumns = BasicColumn.columnList(
			binId, binWarehouseId, binName, zone, row, col, level, slot, binType, binRemark, binDisable,
			binCreateTime, binDisableTime
	);
	BasicColumn[] balanceColumns = BasicColumn.columnList(
			balanceId, balanceSkuId, balanceWarehouseId, availableQty, availableLockQty, defectiveQty,
			defectiveLockQty, toInspectionQty, transferInTransit, balanceCreatedTime, balanceUpdateTime
	);
	BasicColumn[] binBalanceColumns = BasicColumn.columnList(
			binBalanceId, binBalanceSkuId, binBalanceBinId, quantity, lockQty, binBalanceCreatedTime,
			binBalanceUpdateTime
	);
	BasicColumn[] reservationColumns = BasicColumn.columnList(
			reservationId, reservationSkuId, reservationWarehouseId, reservationBinId, parentReservationId,
			reservationTransactionId, reserved, consumed, released, remaining, availableFrom, availableTo,
			reservationCreatedTime, reservationUpdateTime, cancelledTime, releasedTime, reservationRemark
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insertWarehouse(InsertStatementProvider<WarehouseData> insertStatement);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insertBin(InsertStatementProvider<BinData> insertStatement);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	int insertBalanceStatement(InsertStatementProvider<BalanceData> insertStatement);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	int insertBinBalanceStatement(InsertStatementProvider<BinBalanceData> insertStatement);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insertReservationStatement(InsertStatementProvider<ReservationData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "WarehouseResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
			@Result(column = "address", property = "address", jdbcType = JdbcType.VARCHAR),
			@Result(column = "manager", property = "manager", jdbcType = JdbcType.BIGINT),
			@Result(column = "phone", property = "phone", jdbcType = JdbcType.VARCHAR),
			@Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
			@Result(column = "disable", property = "disable", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<WarehouseData> selectWarehouses(SelectStatementProvider selectStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "BinResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "warehouse_id", property = "warehouseId", jdbcType = JdbcType.BIGINT),
			@Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
			@Result(column = "zone", property = "zone", jdbcType = JdbcType.VARCHAR),
			@Result(column = "row", property = "row", jdbcType = JdbcType.VARCHAR),
			@Result(column = "col", property = "col", jdbcType = JdbcType.VARCHAR),
			@Result(column = "level", property = "level", jdbcType = JdbcType.VARCHAR),
			@Result(column = "slot", property = "slot", jdbcType = JdbcType.VARCHAR),
			@Result(column = "bin_type", property = "binType", jdbcType = JdbcType.VARCHAR),
			@Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR),
			@Result(column = "disable", property = "disable", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "disable_time", property = "disableTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<BinData> selectBins(SelectStatementProvider selectStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "BalanceResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "sku_id", property = "skuId", jdbcType = JdbcType.BIGINT),
			@Result(column = "warehouse_id", property = "warehouseId", jdbcType = JdbcType.BIGINT),
			@Result(column = "available_qty", property = "availableQty", jdbcType = JdbcType.BIGINT),
			@Result(column = "available_lock_qty", property = "availableLockQty", jdbcType = JdbcType.BIGINT),
			@Result(column = "defective_qty", property = "defectiveQty", jdbcType = JdbcType.BIGINT),
			@Result(column = "defective_lock_qty", property = "defectiveLockQty", jdbcType = JdbcType.BIGINT),
			@Result(column = "to_inspection_qty", property = "toInspectionQty", jdbcType = JdbcType.BIGINT),
			@Result(column = "transfer_in_transit", property = "transferInTransit", jdbcType = JdbcType.BIGINT),
			@Result(column = "created_time", property = "createdTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<BalanceData> selectBalances(SelectStatementProvider selectStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "BinBalanceResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "sku_id", property = "skuId", jdbcType = JdbcType.BIGINT),
			@Result(column = "bin_id", property = "binId", jdbcType = JdbcType.BIGINT),
			@Result(column = "quantity", property = "quantity", jdbcType = JdbcType.BIGINT),
			@Result(column = "lock_qty", property = "lockQty", jdbcType = JdbcType.BIGINT),
			@Result(column = "created_time", property = "createdTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<BinBalanceData> selectBinBalances(SelectStatementProvider selectStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "ReservationResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "sku_id", property = "skuId", jdbcType = JdbcType.BIGINT),
			@Result(column = "warehouse_id", property = "warehouseId", jdbcType = JdbcType.BIGINT),
			@Result(column = "bin_id", property = "binId", jdbcType = JdbcType.BIGINT),
			@Result(column = "parent_reservation_id", property = "parentReservationId", jdbcType = JdbcType.BIGINT),
			@Result(column = "transaction_id", property = "transactionId", jdbcType = JdbcType.BIGINT),
			@Result(column = "reserved", property = "reserved", jdbcType = JdbcType.BIGINT),
			@Result(column = "consumed", property = "consumed", jdbcType = JdbcType.BIGINT),
			@Result(column = "released", property = "released", jdbcType = JdbcType.BIGINT),
			@Result(column = "remaining", property = "remaining", jdbcType = JdbcType.BIGINT),
			@Result(column = "available_from", property = "availableFrom", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "available_to", property = "availableTo", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "created_time", property = "createdTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "cancelled_time", property = "cancelledTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "released_time", property = "releasedTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "remark", property = "remark", jdbcType = JdbcType.VARCHAR)
	})
	List<ReservationData> selectReservations(SelectStatementProvider selectStatement);

	default Long create(WarehouseData data) {
		MyBatis3Utils.insert(this::insertWarehouse, data, warehouses, c -> c
				.map(warehouseName).toProperty("name")
				.map(address).toPropertyWhenPresent("address", data::address)
				.map(manager).toPropertyWhenPresent("manager", data::manager)
				.map(phone).toPropertyWhenPresent("phone", data::phone)
				.map(description).toPropertyWhenPresent("description", data::description)
		);
		return data.id();
	}

	default Long create(BinData data) {
		MyBatis3Utils.insert(this::insertBin, data, bins, c -> c
				.map(binWarehouseId).toProperty("warehouseId")
				.map(binName).toProperty("name")
				.map(zone).toProperty("zone")
				.map(row).toProperty("row")
				.map(col).toProperty("col")
				.map(level).toProperty("level")
				.map(slot).toProperty("slot")
				.map(binType).toProperty("binType")
				.map(binRemark).toPropertyWhenPresent("remark", data::remark)
		);
		return data.id();
	}

	default List<WarehouseData> query(WarehouseQueryOptions options) {
		WarehouseQueryOptions safeOptions = options == null ? WarehouseQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectWarehouses, warehouseColumns, warehouses, c -> c
				.where(warehouseId, isEqualToWhenPresent(safeOptions::getIdValue))
				.orderBy(warehouseId)
		);
	}

	default List<BinData> query(BinQueryOptions options) {
		BinQueryOptions safeOptions = options == null ? BinQueryOptions.none() : options;
		if (safeOptions.hasIds()) {
			return MyBatis3Utils.selectList(this::selectBins, binColumns, bins, c -> c
					.where(binId, isIn(safeOptions.getIdsValue()))
					.orderBy(binId)
			);
		}
		return MyBatis3Utils.selectList(this::selectBins, binColumns, bins, c -> c
				.where(binId, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(binWarehouseId, isEqualToWhenPresent(safeOptions::getWarehouseIdValue))
				.orderBy(binId)
		);
	}

	default List<BalanceData> query(BalanceQueryOptions options) {
		BalanceQueryOptions safeOptions = options == null ? BalanceQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectBalances, balanceColumns, balances, c -> c
				.where(balanceSkuId, isEqualToWhenPresent(safeOptions::getSkuIdValue))
				.and(balanceWarehouseId, isEqualToWhenPresent(safeOptions::getWarehouseIdValue))
				.orderBy(balanceId)
		);
	}

	default List<BinBalanceData> query(BinBalanceQueryOptions options) {
		BinBalanceQueryOptions safeOptions = options == null ? BinBalanceQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectBinBalances, binBalanceColumns, binBalances, c -> c
				.where(binBalanceSkuId, isEqualToWhenPresent(safeOptions::getSkuIdValue))
				.and(binBalanceBinId, isEqualToWhenPresent(safeOptions::getBinIdValue))
				.orderBy(binBalanceId)
		);
	}

	default int create(BalanceData data) {
		return MyBatis3Utils.insert(this::insertBalanceStatement, data, balances, c -> c
				.map(balanceSkuId).toProperty("skuId")
				.map(balanceWarehouseId).toProperty("warehouseId")
				.map(availableQty).toProperty("availableQty")
				.map(availableLockQty).toProperty("availableLockQty")
				.map(defectiveQty).toProperty("defectiveQty")
				.map(defectiveLockQty).toProperty("defectiveLockQty")
				.map(toInspectionQty).toProperty("toInspectionQty")
				.map(transferInTransit).toProperty("transferInTransit")
		);
	}

	default int updateBalance(BalanceData data) {
		return MyBatis3Utils.update(this::updateStatement, balances, c -> c
				.set(availableQty).equalTo(data::availableQty)
				.set(availableLockQty).equalTo(data::availableLockQty)
				.set(defectiveQty).equalTo(data::defectiveQty)
				.set(defectiveLockQty).equalTo(data::defectiveLockQty)
				.set(toInspectionQty).equalTo(data::toInspectionQty)
				.set(transferInTransit).equalTo(data::transferInTransit)
				.set(balanceUpdateTime).equalToConstant("NOW()")
				.where(balanceId, isEqualTo(data::id))
		);
	}

	default int create(BinBalanceData data) {
		return MyBatis3Utils.insert(this::insertBinBalanceStatement, data, binBalances, c -> c
				.map(binBalanceSkuId).toProperty("skuId")
				.map(binBalanceBinId).toProperty("binId")
				.map(quantity).toProperty("quantity")
				.map(lockQty).toProperty("lockQty")
		);
	}

	default int updateBinBalance(BinBalanceData data) {
		return MyBatis3Utils.update(this::updateStatement, binBalances, c -> c
				.set(quantity).equalTo(data::quantity)
				.set(lockQty).equalTo(data::lockQty)
				.set(binBalanceUpdateTime).equalToConstant("NOW()")
				.where(binBalanceId, isEqualTo(data::id))
		);
	}

	default Long create(ReservationData data) {
		MyBatis3Utils.insert(this::insertReservationStatement, data, reservations, c -> c
				.map(reservationSkuId).toProperty("skuId")
				.map(reservationWarehouseId).toProperty("warehouseId")
				.map(reservationBinId).toProperty("binId")
				.map(parentReservationId).toPropertyWhenPresent("parentReservationId", data::parentReservationId)
				.map(reservationTransactionId).toPropertyWhenPresent("transactionId", data::transactionId)
				.map(reserved).toProperty("reserved")
				.map(consumed).toProperty("consumed")
				.map(released).toProperty("released")
				.map(remaining).toProperty("remaining")
				.map(availableFrom).toPropertyWhenPresent("availableFrom", data::availableFrom)
				.map(availableTo).toPropertyWhenPresent("availableTo", data::availableTo)
				.map(reservationRemark).toPropertyWhenPresent("remark", data::remark)
		);
		return data.id();
	}

	default List<ReservationData> query(ReservationQueryOptions options) {
		ReservationQueryOptions safeOptions = options == null ? ReservationQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectReservations, reservationColumns, reservations, c -> c
				.where(reservationId, isEqualToWhenPresent(safeOptions::getIdValue))
				.orderBy(reservationId)
		);
	}

	@Select("""
			<script>
			SELECT *
			FROM inv_balances
			WHERE
			<foreach collection="keys" item="key" separator=" OR ">
			  (sku_id = #{key.skuId} AND warehouse_id = #{key.warehouseId})
			</foreach>
			ORDER BY sku_id, warehouse_id
			FOR UPDATE
			</script>
			""")
	@ResultMap("BalanceResult")
	List<BalanceData> queryBalancesForUpdate(@Param("keys") List<BalanceKeyParam> keys);

	@Select("""
			<script>
			SELECT *
			FROM inv_bin_balances
			WHERE
			<foreach collection="keys" item="key" separator=" OR ">
			  (sku_id = #{key.skuId} AND bin_id = #{key.binId})
			</foreach>
			ORDER BY sku_id, bin_id
			FOR UPDATE
			</script>
			""")
	@ResultMap("BinBalanceResult")
	List<BinBalanceData> queryBinBalancesForUpdate(@Param("keys") List<BinBalanceKeyParam> keys);

	@Select("""
			<script>
			SELECT *
			FROM inv_bin_balances
			WHERE
			<foreach collection="keys" item="key" separator=" OR ">
			  (sku_id = #{key.skuId} AND bin_id = #{key.binId})
			</foreach>
			</script>
			""")
	@ResultMap("BinBalanceResult")
	List<BinBalanceData> queryBinBalances(@Param("keys") List<BinBalanceKeyParam> keys);

	@Select("""
			INSERT INTO inv_transactions (source_order_id, linked_order_ids, transaction_type, operator_id, remark)
			VALUES (#{sourceOrderId}, CAST(#{linkedOrderIds} AS BIGINT[]), #{transactionType}, #{operatorId}, #{remark})
			RETURNING id
			""")
	Long createTransaction(
			@Param("sourceOrderId") Long sourceOrderId,
			@Param("linkedOrderIds") String linkedOrderIds,
			@Param("transactionType") String transactionType,
			@Param("operatorId") Long operatorId,
			@Param("remark") String remark
	);

	@Insert("""
			INSERT INTO inv_warehouse_ledgers
			  (sku_id, warehouse_id, warehouse_ledger_type, available_delta, available_lock_delta,
			   defective_delta, defective_lock_delta, to_inspection_delta, transfer_in_transit_delta,
			   available_balance, available_lock_balance, defective_balance, defective_lock_balance,
			   to_inspection_balance, transfer_in_transit_balance, transaction_id, remark)
			VALUES
			  (#{skuId}, #{warehouseId}, #{ledgerType}, #{availableDelta}, #{availableLockDelta},
			   #{defectiveDelta}, #{defectiveLockDelta}, #{toInspectionDelta}, #{transferInTransitDelta},
			   #{availableBalance}, #{availableLockBalance}, #{defectiveBalance}, #{defectiveLockBalance},
			   #{toInspectionBalance}, #{transferInTransitBalance}, #{transactionId}, #{remark})
			""")
	int saveWarehouseLedger(
			@Param("skuId") Long skuId,
			@Param("warehouseId") Long warehouseId,
			@Param("ledgerType") String ledgerType,
			@Param("availableDelta") long availableDelta,
			@Param("availableLockDelta") long availableLockDelta,
			@Param("defectiveDelta") long defectiveDelta,
			@Param("defectiveLockDelta") long defectiveLockDelta,
			@Param("toInspectionDelta") long toInspectionDelta,
			@Param("transferInTransitDelta") long transferInTransitDelta,
			@Param("availableBalance") long availableBalance,
			@Param("availableLockBalance") long availableLockBalance,
			@Param("defectiveBalance") long defectiveBalance,
			@Param("defectiveLockBalance") long defectiveLockBalance,
			@Param("toInspectionBalance") long toInspectionBalance,
			@Param("transferInTransitBalance") long transferInTransitBalance,
			@Param("transactionId") Long transactionId,
			@Param("remark") String remark
	);

	@Insert("""
			INSERT INTO inv_bin_ledgers
			  (reservation_id, sku_id, bin_id, bin_ledger_type, delta, lock_delta, balance, lock_balance,
			   transaction_id, remark)
			VALUES
			  (#{reservationId}, #{skuId}, #{binId}, #{ledgerType}, #{delta}, #{lockDelta}, #{balance},
			   #{lockBalance}, #{transactionId}, #{remark})
			""")
	int saveBinLedger(
			@Param("reservationId") Long reservationId,
			@Param("skuId") Long skuId,
			@Param("binId") Long binId,
			@Param("ledgerType") String ledgerType,
			@Param("delta") long delta,
			@Param("lockDelta") long lockDelta,
			@Param("balance") long balance,
			@Param("lockBalance") long lockBalance,
			@Param("transactionId") Long transactionId,
			@Param("remark") String remark
	);

	@Update("""
			UPDATE inv_reservations
			SET consumed = consumed + #{consumed},
			    remaining = remaining - #{consumed},
			    transaction_id = #{transactionId},
			    update_time = NOW()
			WHERE id = #{reservationId}
			  AND remaining >= #{consumed}
			  AND cancelled_time IS NULL
			""")
	int consumeReservation(
			@Param("reservationId") Long reservationId,
			@Param("consumed") long consumed,
			@Param("transactionId") Long transactionId
	);

	@Update("""
			<script>
			UPDATE inv_reservations
			SET cancelled_time = NOW(), released_time = NOW(), update_time = NOW()
			WHERE id IN
			<foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
			</script>
			""")
	int cancelReservations(@Param("ids") List<Long> ids);

	@Insert("""
			INSERT INTO inv_sequence_numbers (number_type, date_key, current_sequence)
			VALUES (#{numberType}, #{dateKey}, 0)
			ON CONFLICT (number_type, date_key) DO NOTHING
			""")
	int ensureSequence(@Param("numberType") String numberType, @Param("dateKey") String dateKey);

	@Select("""
			UPDATE inv_sequence_numbers
			SET current_sequence = current_sequence + 1
			WHERE number_type = #{numberType} AND date_key = #{dateKey}
			RETURNING current_sequence
			""")
	Integer incrementSequence(@Param("numberType") String numberType, @Param("dateKey") String dateKey);

	@Select("""
			INSERT INTO inv_receipts (number, type_key, creator)
			VALUES (#{number}, #{typeKey}, #{operatorId})
			RETURNING id
			""")
	Long createReceipt(
			@Param("number") String number,
			@Param("typeKey") String typeKey,
			@Param("operatorId") Long operatorId
	);

	@Select("SELECT id FROM inv_receipts WHERE id = #{receiptId} FOR UPDATE")
	Long lockReceiptForUpdate(@Param("receiptId") Long receiptId);

	@Update("""
			UPDATE inv_receipt_records
			SET disable_time = NOW()
			WHERE receipt_id = #{receiptId} AND disable_time IS NULL
			""")
	int disableCurrentReceiptRecord(@Param("receiptId") Long receiptId);

	@Insert("""
			INSERT INTO inv_receipt_records (receipt_id, state, payload, operator)
			VALUES (#{receiptId}, #{state}, CAST(#{payload} AS JSONB), #{operatorId})
			""")
	int createReceiptRecord(
			@Param("receiptId") Long receiptId,
			@Param("state") String state,
			@Param("payload") String payload,
			@Param("operatorId") Long operatorId
	);

	@Select("""
			SELECT r.id, r.number, r.type_key, r.creator, r.create_time, rr.state, rr.payload, rr.operator,
			       rr.record_time
			FROM inv_receipts r
			JOIN inv_receipt_records rr ON rr.receipt_id = r.id AND rr.disable_time IS NULL
			WHERE r.id = #{receiptId} AND r.delete_time IS NULL
			""")
	@Results(id = "ReceiptRowResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "number", property = "number", jdbcType = JdbcType.VARCHAR),
			@Result(column = "type_key", property = "typeKey", jdbcType = JdbcType.VARCHAR),
			@Result(column = "creator", property = "creator", jdbcType = JdbcType.BIGINT),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "state", property = "state", jdbcType = JdbcType.VARCHAR),
			@Result(column = "payload", property = "payload", jdbcType = JdbcType.VARCHAR),
			@Result(column = "operator", property = "operator", jdbcType = JdbcType.BIGINT),
			@Result(column = "record_time", property = "recordTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	ReceiptRowData queryReceipt(@Param("receiptId") Long receiptId);

	@Select("""
			<script>
			SELECT r.id, r.number, r.type_key, r.creator, r.create_time, rr.state, rr.payload, rr.operator,
			       rr.record_time
			FROM inv_receipts r
			JOIN inv_receipt_records rr ON rr.receipt_id = r.id AND rr.disable_time IS NULL
			WHERE r.delete_time IS NULL
			<if test="typeKey != null and typeKey != ''">
			  AND r.type_key = #{typeKey}
			</if>
			ORDER BY r.id DESC
			LIMIT #{limit} OFFSET #{offset}
			</script>
			""")
	@ResultMap("ReceiptRowResult")
	List<ReceiptRowData> listReceipts(
			@Param("typeKey") String typeKey,
			@Param("limit") int limit,
			@Param("offset") int offset
	);

	@Select("""
			<script>
			SELECT COUNT(*)
			FROM inv_receipts
			WHERE delete_time IS NULL
			<if test="typeKey != null and typeKey != ''">
			  AND type_key = #{typeKey}
			</if>
			</script>
			""")
	long countReceipts(@Param("typeKey") String typeKey);

	record BalanceKeyParam(Long skuId, Long warehouseId) {}

	record BinBalanceKeyParam(Long skuId, Long binId) {}
}

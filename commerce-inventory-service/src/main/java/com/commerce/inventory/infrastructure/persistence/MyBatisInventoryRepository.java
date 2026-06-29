package com.commerce.inventory.infrastructure.persistence;

import com.commerce.inventory.domain.model.InventoryException;
import com.commerce.inventory.domain.model.BalanceQueryOptions;
import com.commerce.inventory.domain.model.BinBalanceQueryOptions;
import com.commerce.inventory.domain.model.BinQueryOptions;
import com.commerce.inventory.domain.model.InventoryModels.Balance;
import com.commerce.inventory.domain.model.InventoryModels.Bin;
import com.commerce.inventory.domain.model.InventoryModels.BinBalance;
import com.commerce.inventory.domain.model.InventoryModels.BinLedgerType;
import com.commerce.inventory.domain.model.InventoryModels.BinQuantity;
import com.commerce.inventory.domain.model.InventoryModels.BinType;
import com.commerce.inventory.domain.model.InventoryModels.CreateBinRequest;
import com.commerce.inventory.domain.model.InventoryModels.CreateWarehouseRequest;
import com.commerce.inventory.domain.model.InventoryModels.NumberType;
import com.commerce.inventory.domain.model.InventoryModels.Quantity;
import com.commerce.inventory.domain.model.InventoryModels.Reservation;
import com.commerce.inventory.domain.model.InventoryModels.TransactionType;
import com.commerce.inventory.domain.model.InventoryModels.Warehouse;
import com.commerce.inventory.domain.model.InventoryModels.WarehouseLedgerType;
import com.commerce.inventory.domain.model.ReservationQueryOptions;
import com.commerce.inventory.domain.model.WarehouseQueryOptions;
import com.commerce.inventory.infrastructure.persistence.mapper.InventoryMyBatisMapper;
import com.commerce.inventory.infrastructure.persistence.mapper.InventoryMyBatisMapper.BalanceKeyParam;
import com.commerce.inventory.infrastructure.persistence.mapper.InventoryMyBatisMapper.BinBalanceKeyParam;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.BalanceData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.BinBalanceData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.BinData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.ReceiptRowData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.ReservationData;
import com.commerce.inventory.infrastructure.persistence.model.InventoryData.WarehouseData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisInventoryRepository {

	public record BalanceKey(Long skuId, Long warehouseId) implements Comparable<BalanceKey> {
		@Override
		public int compareTo(BalanceKey other) {
			int skuCompare = skuId.compareTo(other.skuId);
			return skuCompare != 0 ? skuCompare : warehouseId.compareTo(other.warehouseId);
		}
	}

	public record BinBalanceKey(Long skuId, Long binId) implements Comparable<BinBalanceKey> {
		@Override
		public int compareTo(BinBalanceKey other) {
			int skuCompare = skuId.compareTo(other.skuId);
			return skuCompare != 0 ? skuCompare : binId.compareTo(other.binId);
		}
	}

	public record BinInfo(Long warehouseId, BinType binType, boolean disable) {}

	public record ReservationCreate(Long skuId, Long warehouseId, Long binId, Long parentReservationId,
			Long transactionId, long quantity, ZonedDateTime availableFrom, ZonedDateTime availableTo, String remark) {}

	public record ReceiptRow(Long id, String number, String typeKey, Long creator, ZonedDateTime createTime,
			String state, JsonNode payload, Long operator, ZonedDateTime recordTime) {}

	private final InventoryMyBatisMapper mapper;
	private final ObjectMapper objectMapper;

	public MyBatisInventoryRepository(InventoryMyBatisMapper mapper, ObjectMapper objectMapper) {
		this.mapper = mapper;
		this.objectMapper = objectMapper;
	}

	public Long create(CreateWarehouseRequest request) {
		return mapper.create(new WarehouseData(
				null,
				request.name(),
				request.address(),
				request.manager(),
				request.phone(),
				request.description(),
				null,
				null
		));
	}

	public Long create(CreateBinRequest request) {
		return mapper.create(new BinData(
				null,
				request.warehouseId(),
				request.name(),
				request.zone(),
				request.row(),
				request.col(),
				request.level(),
				request.slot(),
				request.binType().name(),
				request.remark(),
				null,
				null,
				null
		));
	}

	public List<Warehouse> queryWarehouses(WarehouseQueryOptions options) {
		return mapper.query(options).stream().map(this::toWarehouse).toList();
	}

	public List<Bin> queryBins(BinQueryOptions options) {
		return mapper.query(options).stream().map(this::toBin).toList();
	}

	public Map<Long, BinInfo> queryBinInfos(BinQueryOptions options) {
		if (options == null) {
			return Map.of();
		}
		if (options.getIdsValue() != null && options.getIdsValue().isEmpty()) {
			return Map.of();
		}
		Map<Long, BinInfo> result = new HashMap<>();
		for (BinData row : mapper.query(options)) {
			result.put(row.id(), new BinInfo(
					row.warehouseId(),
					BinType.valueOf(row.binType()),
					Boolean.TRUE.equals(row.disable())
			));
		}
		return result;
	}

	public List<Balance> queryBalances(BalanceQueryOptions options) {
		return mapper.query(options).stream().map(this::toBalance).toList();
	}

	public List<BinBalance> queryBinBalances(BinBalanceQueryOptions options) {
		return mapper.query(options).stream().map(this::toBinBalance).toList();
	}

	public Map<BalanceKey, Balance> queryBalancesForUpdate(List<BalanceKey> keys) {
		List<BalanceKey> sortedKeys = sortedBalanceKeys(keys);
		if (sortedKeys.isEmpty()) {
			return Map.of();
		}
		Map<BalanceKey, Balance> result = new LinkedHashMap<>();
		for (BalanceData row : mapper.queryBalancesForUpdate(sortedKeys.stream()
				.map(key -> new BalanceKeyParam(key.skuId(), key.warehouseId()))
				.toList())) {
			Balance balance = toBalance(row);
			result.put(new BalanceKey(balance.skuId(), balance.warehouseId()), balance);
		}
		return result;
	}

	public Map<BinBalanceKey, BinBalance> queryBinBalancesForUpdate(List<BinBalanceKey> keys) {
		List<BinBalanceKey> sortedKeys = sortedBinBalanceKeys(keys);
		if (sortedKeys.isEmpty()) {
			return Map.of();
		}
		Map<BinBalanceKey, BinBalance> result = new LinkedHashMap<>();
		for (BinBalanceData row : mapper.queryBinBalancesForUpdate(sortedKeys.stream()
				.map(key -> new BinBalanceKeyParam(key.skuId(), key.binId()))
				.toList())) {
			BinBalance balance = toBinBalance(row);
			result.put(new BinBalanceKey(balance.skuId(), balance.binId()), balance);
		}
		return result;
	}

	public Map<BinBalanceKey, BinBalance> queryBinBalances(List<BinBalanceKey> keys) {
		List<BinBalanceKey> sortedKeys = sortedBinBalanceKeys(keys);
		if (sortedKeys.isEmpty()) {
			return Map.of();
		}
		Map<BinBalanceKey, BinBalance> result = new LinkedHashMap<>();
		for (BinBalanceData row : mapper.queryBinBalances(sortedKeys.stream()
				.map(key -> new BinBalanceKeyParam(key.skuId(), key.binId()))
				.toList())) {
			BinBalance balance = toBinBalance(row);
			result.put(new BinBalanceKey(balance.skuId(), balance.binId()), balance);
		}
		return result;
	}

	public void saveOrUpdateBalance(Balance balance, boolean exists) {
		BalanceData data = new BalanceData(
				balance.id(),
				balance.skuId(),
				balance.warehouseId(),
				balance.availableQty(),
				balance.availableLockQty(),
				balance.defectiveQty(),
				balance.defectiveLockQty(),
				balance.toInspectionQty(),
				balance.transferInTransit(),
				balance.createdTime(),
				balance.updateTime()
		);
		if (exists) {
			mapper.updateBalance(data);
		}
		else {
			mapper.create(data);
		}
	}

	public void saveOrUpdateBinBalance(BinBalance balance, boolean exists) {
		BinBalanceData data = new BinBalanceData(
				balance.id(),
				balance.skuId(),
				balance.binId(),
				balance.quantity(),
				balance.lockQty(),
				balance.createdTime(),
				balance.updateTime()
		);
		if (exists) {
			mapper.updateBinBalance(data);
		}
		else {
			mapper.create(data);
		}
	}

	public Long createTransaction(Long sourceOrderId, TransactionType transactionType, Long operatorId, String remark,
			List<Long> linkedOrderIds) {
		return mapper.createTransaction(sourceOrderId, toPgArrayLiteral(linkedOrderIds), transactionType.name(), operatorId,
				remark);
	}

	public void saveWarehouseLedger(Long skuId, Long warehouseId, WarehouseLedgerType ledgerType, Quantity delta,
			Balance balance, Long transactionId, String remark) {
		mapper.saveWarehouseLedger(skuId, warehouseId, ledgerType.name(), delta.available(), delta.availableLock(),
				delta.defective(), delta.defectiveLock(), delta.toInspection(), delta.transferInTransit(),
				balance.availableQty(), balance.availableLockQty(), balance.defectiveQty(), balance.defectiveLockQty(),
				balance.toInspectionQty(), balance.transferInTransit(), transactionId, remark);
	}

	public void saveBinLedger(Long skuId, Long binId, BinLedgerType ledgerType, BinQuantity delta, BinBalance balance,
			Long reservationId, Long transactionId, String remark) {
		mapper.saveBinLedger(reservationId, skuId, binId, ledgerType.name(), delta.quantity(), delta.lockQty(),
				balance.quantity(), balance.lockQty(), transactionId, remark);
	}

	public List<Long> createReservations(List<ReservationCreate> reservations) {
		return reservations.stream()
				.map(row -> mapper.create(new ReservationData(
						null,
						row.skuId(),
						row.warehouseId(),
						row.binId(),
						row.parentReservationId(),
						row.transactionId(),
						row.quantity(),
						0L,
						0L,
						row.quantity(),
						row.availableFrom(),
						row.availableTo(),
						null,
						null,
						null,
						null,
						row.remark()
				)))
				.toList();
	}

	public List<Reservation> queryReservations(ReservationQueryOptions options) {
		return mapper.query(options).stream().map(this::toReservation).toList();
	}

	public void consumeReservation(Long reservationId, long consumed, Long transactionId) {
		int updated = mapper.consumeReservation(reservationId, consumed, transactionId);
		if (updated == 0) {
			throw InventoryException.versionConflict(reservationId);
		}
	}

	public void cancelReservations(List<Long> reservationIds) {
		if (reservationIds != null && !reservationIds.isEmpty()) {
			mapper.cancelReservations(reservationIds);
		}
	}

	public void bindReservations(List<Long> reservationIds) {
		if (reservationIds != null && !reservationIds.isEmpty()) {
			mapper.bindReservations(reservationIds);
		}
	}

	public List<Long> queryExpiredUnboundReservationIds(int limit) {
		return mapper.queryExpiredUnboundReservationIds(limit);
	}

	public String nextReceiptNumber(NumberType numberType) {
		LocalDate today = LocalDate.now();
		String dateKey = today.format(DateTimeFormatter.BASIC_ISO_DATE);
		mapper.ensureSequence(numberType.name(), dateKey);
		Integer next = mapper.incrementSequence(numberType.name(), dateKey);
		return numberType.prefix() + today.format(DateTimeFormatter.ofPattern("yyMMdd")) + "%04d".formatted(next);
	}

	public Long createReceipt(String number, String typeKey, Long operatorId) {
		return mapper.createReceipt(number, typeKey, operatorId);
	}

	public void lockReceiptForUpdate(Long receiptId) {
		mapper.lockReceiptForUpdate(receiptId);
	}

	public void createReceiptRecord(Long receiptId, String state, JsonNode payload, Long operatorId) {
		lockReceiptForUpdate(receiptId);
		mapper.disableCurrentReceiptRecord(receiptId);
		mapper.createReceiptRecord(receiptId, state, payload.toString(), operatorId);
	}

	public Optional<ReceiptRow> queryReceipt(Long receiptId) {
		return Optional.ofNullable(mapper.queryReceipt(receiptId)).map(this::toReceiptRow);
	}

	public List<ReceiptRow> listReceipts(String typeKey, int pageNumber, int pageSize) {
		int limit = Math.max(pageSize, 1);
		int offset = Math.max(pageNumber - 1, 0) * limit;
		return mapper.listReceipts(typeKey, limit, offset).stream().map(this::toReceiptRow).toList();
	}

	public long countReceipts(String typeKey) {
		return mapper.countReceipts(typeKey);
	}

	private Warehouse toWarehouse(WarehouseData row) {
		return new Warehouse(row.id(), row.name(), row.address(), row.manager(), row.phone(), row.description(),
				Boolean.TRUE.equals(row.disable()), row.createTime());
	}

	private Bin toBin(BinData row) {
		return new Bin(row.id(), row.warehouseId(), row.name(), row.zone(), row.row(), row.col(), row.level(),
				row.slot(), BinType.valueOf(row.binType()), row.remark(), Boolean.TRUE.equals(row.disable()),
				row.createTime(), row.disableTime());
	}

	private Balance toBalance(BalanceData row) {
		return new Balance(row.id(), row.skuId(), row.warehouseId(), row.availableQty(), row.availableLockQty(),
				row.defectiveQty(), row.defectiveLockQty(), row.toInspectionQty(), row.transferInTransit(),
				row.createdTime(), row.updateTime());
	}

	private BinBalance toBinBalance(BinBalanceData row) {
		return new BinBalance(row.id(), row.skuId(), row.binId(), row.quantity(), row.lockQty(), row.createdTime(),
				row.updateTime());
	}

	private Reservation toReservation(ReservationData row) {
		return new Reservation(row.id(), row.skuId(), row.warehouseId(), row.binId(), row.parentReservationId(),
				row.transactionId(), row.reserved(), row.consumed(), row.released(), row.remaining(), row.availableFrom(),
				row.availableTo(), row.createdTime(), row.updateTime(), row.cancelledTime(), row.releasedTime(),
				row.remark());
	}

	private ReceiptRow toReceiptRow(ReceiptRowData row) {
		return new ReceiptRow(row.id(), row.number(), row.typeKey(), row.creator(), row.createTime(), row.state(),
				readJson(row.payload()), row.operator(), row.recordTime());
	}

	private JsonNode readJson(String value) {
		try {
			return objectMapper.readTree(value);
		}
		catch (Exception ex) {
			throw new IllegalStateException("invalid receipt payload json", ex);
		}
	}

	private static String toPgArrayLiteral(List<Long> values) {
		if (values == null || values.isEmpty()) {
			return "{}";
		}
		return "{" + String.join(",", values.stream().map(String::valueOf).toList()) + "}";
	}

	private static List<BalanceKey> sortedBalanceKeys(List<BalanceKey> keys) {
		return keys == null ? List.of() : keys.stream().filter(Objects::nonNull).distinct().sorted().toList();
	}

	private static List<BinBalanceKey> sortedBinBalanceKeys(List<BinBalanceKey> keys) {
		return keys == null ? List.of() : keys.stream().filter(Objects::nonNull).distinct().sorted().toList();
	}
}

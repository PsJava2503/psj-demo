package com.commerce.inventory.application.service;

import com.commerce.inventory.application.port.InventoryUseCase;
import com.commerce.inventory.domain.model.BalanceQueryOptions;
import com.commerce.inventory.domain.model.BinBalanceQueryOptions;
import com.commerce.inventory.domain.model.BinQueryOptions;
import com.commerce.inventory.domain.model.InventoryException;
import com.commerce.inventory.domain.model.InventoryModels.AdjustLockedRequest;
import com.commerce.inventory.domain.model.InventoryModels.AdjustRequest;
import com.commerce.inventory.domain.model.InventoryModels.Balance;
import com.commerce.inventory.domain.model.InventoryModels.Bin;
import com.commerce.inventory.domain.model.InventoryModels.BinBalance;
import com.commerce.inventory.domain.model.InventoryModels.BinLedgerType;
import com.commerce.inventory.domain.model.InventoryModels.BinQuantity;
import com.commerce.inventory.domain.model.InventoryModels.CreateReceiptRequest;
import com.commerce.inventory.domain.model.InventoryModels.CreateBinRequest;
import com.commerce.inventory.domain.model.InventoryModels.CreateWarehouseRequest;
import com.commerce.inventory.domain.model.InventoryModels.InBounding;
import com.commerce.inventory.domain.model.InventoryModels.InventoryReceipt;
import com.commerce.inventory.domain.model.InventoryModels.InboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.LockRequest;
import com.commerce.inventory.domain.model.InventoryModels.Locking;
import com.commerce.inventory.domain.model.InventoryModels.OutBounding;
import com.commerce.inventory.domain.model.InventoryModels.OutboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.PageResponse;
import com.commerce.inventory.domain.model.InventoryModels.Quantity;
import com.commerce.inventory.domain.model.InventoryModels.ReceiptType;
import com.commerce.inventory.domain.model.InventoryModels.Reservation;
import com.commerce.inventory.domain.model.InventoryModels.TransactionType;
import com.commerce.inventory.domain.model.InventoryModels.TransitReceiptRequest;
import com.commerce.inventory.domain.model.InventoryModels.UnlockRequest;
import com.commerce.inventory.domain.model.InventoryModels.WarehouseLedgerType;
import com.commerce.inventory.domain.model.InventoryModels.Warehouse;
import com.commerce.inventory.domain.model.ReservationQueryOptions;
import com.commerce.inventory.domain.model.WarehouseQueryOptions;
import com.commerce.inventory.infrastructure.persistence.MyBatisInventoryRepository;
import com.commerce.inventory.infrastructure.persistence.MyBatisInventoryRepository.BalanceKey;
import com.commerce.inventory.infrastructure.persistence.MyBatisInventoryRepository.BinBalanceKey;
import com.commerce.inventory.infrastructure.persistence.MyBatisInventoryRepository.BinInfo;
import com.commerce.inventory.infrastructure.persistence.MyBatisInventoryRepository.ReservationCreate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryApplicationService implements InventoryUseCase {

	private static final Long SYSTEM_OPERATOR_ID = 0L;

	private final MyBatisInventoryRepository repository;
	private final InventoryReceiptService receiptService;

	public InventoryApplicationService(MyBatisInventoryRepository repository, InventoryReceiptService receiptService) {
		this.repository = repository;
		this.receiptService = receiptService;
	}

	@Override
	@Transactional
	public boolean deductStock(Long productId, Integer quantity) {
		return productId != null && quantity != null && quantity > 0;
	}

	@Override
	public boolean available(Long productId) {
		return productId != null;
	}

	@Override
	@Transactional
	public List<Long> reserveStock(Long skuId, Integer quantity, Long orderId) {
		if (skuId == null || quantity == null || quantity <= 0) {
			return List.of();
		}
		List<BinBalance> candidates = repository.queryBinBalances(new BinBalanceQueryOptions(
				Optional.of(skuId),
				Optional.empty()
		)).stream()
				.filter(balance -> balance.quantity() - balance.lockQty() > 0)
				.sorted(Comparator.comparing(BinBalance::id))
				.toList();
		Map<Long, BinInfo> binInfos = repository.queryBinInfos(
				BinQueryOptions.byIds(candidates.stream().map(BinBalance::binId).distinct().toList()));
		long remaining = quantity;
		List<Locking> lockings = new ArrayList<>();
		for (BinBalance balance : candidates) {
			if (remaining <= 0) {
				break;
			}
			BinInfo binInfo = binInfos.get(balance.binId());
			if (binInfo == null || binInfo.disable()) {
				continue;
			}
			long availableQuantity = balance.quantity() - balance.lockQty();
			long lockQuantity = Math.min(remaining, availableQuantity);
			lockings.add(new Locking(skuId, balance.binId(), binInfo.warehouseId(), lockQuantity));
			remaining -= lockQuantity;
		}
		if (remaining > 0) {
			return List.of();
		}
		return lock(new LockRequest(
				TransactionType.OutSalesDelivery,
				lockings,
				orderId,
				SYSTEM_OPERATOR_ID,
				"reserve order stock",
				"reserve order stock",
				List.of()
		));
	}

	@Override
	@Transactional
	public void releaseStock(List<Long> reservationIds, Long orderId) {
		List<OutBounding> items = reservationItems(reservationIds);
		if (items.isEmpty()) {
			return;
		}
		unlock(new UnlockRequest(
				TransactionType.OutSalesDelivery,
				items,
				orderId,
				SYSTEM_OPERATOR_ID,
				"release order stock",
				"release order stock",
				true,
				List.of()
		));
	}

	@Override
	@Transactional
	public void confirmStock(List<Long> reservationIds, Long orderId) {
		List<OutBounding> items = reservationItems(reservationIds);
		if (items.isEmpty()) {
			return;
		}
		outbound(new OutboundRequest(
				TransactionType.OutSalesDelivery,
				items,
				orderId,
				SYSTEM_OPERATOR_ID,
				"confirm order stock",
				List.of()
		));
	}

	private List<OutBounding> reservationItems(List<Long> reservationIds) {
		return safeList(reservationIds).stream()
				.map(reservationId -> repository.queryReservations(ReservationQueryOptions.byId(reservationId)).stream()
						.findFirst()
						.orElse(null))
				.filter(Objects::nonNull)
				.filter(reservation -> reservation.remaining() > 0)
				.map(reservation -> new OutBounding(
						reservation.skuId(),
						reservation.binId(),
						reservation.remaining(),
						reservation.id(),
						null
				))
				.toList();
	}

	@Override
	@Transactional
	public void inbound(InboundRequest request) {
		List<InBounding> items = safeList(request.items());
		if (items.isEmpty()) {
			return;
		}
		Map<Long, BinInfo> binInfos = validateBins(items.stream().map(InBounding::binId).distinct().toList(), true);
		List<WarehouseDelta> warehouseDeltas = new ArrayList<>();
		List<BinDelta> binDeltas = new ArrayList<>();
		for (InBounding item : items) {
			BinInfo binInfo = binInfos.get(item.binId());
			Quantity delta = binInfo.binType().inboundDelta(item.quantity());
			if (request.transactionType() == TransactionType.InTransfer) {
				delta = delta.combine(new Quantity(0, 0, 0, 0, 0, -item.quantity()));
			}
			warehouseDeltas.add(new WarehouseDelta(item.skuId(), binInfo.warehouseId(), delta));
			binDeltas.add(new BinDelta(item.skuId(), item.binId(), BinQuantity.quantityOnly(item.quantity())));
		}
		MutationState state = processInventoryUpdate(aggregateWarehouse(warehouseDeltas), aggregateBin(binDeltas));
		Long transactionId = createTransactionAndPersist(request.sourceOrderId(), request.transactionType(),
				request.operatorId(), request.remark(), request.linkedOrderIds(), state);
		saveLedgers(state.warehouseDemands(), state.binDemands(), state.updatedBalances(), state.updatedBinBalances(),
				request.transactionType(), transactionId, null, true, null, null);
	}

	@Override
	@Transactional
	public void outbound(OutboundRequest request) {
		List<OutBounding> items = safeList(request.items());
		if (items.isEmpty()) {
			return;
		}
		if (request.transactionType() == TransactionType.OutTransfer
				&& items.stream().anyMatch(item -> item.toWarehouseId() == null)) {
			throw new InventoryException("missing target warehouse for transfer: " + request.sourceOrderId());
		}
		Map<Long, BinInfo> binInfos = validateBins(items.stream().map(OutBounding::binId).distinct().toList(), false);
		List<WarehouseDelta> warehouseDeltas = new ArrayList<>();
		List<BinDelta> binDeltas = new ArrayList<>();
		for (OutBounding item : items) {
			BinInfo binInfo = binInfos.get(item.binId());
			warehouseDeltas.add(new WarehouseDelta(item.skuId(), binInfo.warehouseId(),
					binInfo.binType().outboundDelta(item.quantity())));
			if (request.transactionType() == TransactionType.OutTransfer) {
				warehouseDeltas.add(new WarehouseDelta(item.skuId(), item.toWarehouseId(),
						new Quantity(0, 0, 0, 0, 0, item.quantity())));
			}
			binDeltas.add(new BinDelta(item.skuId(), item.binId(), new BinQuantity(-item.quantity(), -item.quantity())));
		}
		Map<BalanceKey, Quantity> warehouseDemands = aggregateWarehouse(warehouseDeltas);
		Map<BinBalanceKey, BinQuantity> binDemands = aggregateBin(binDeltas);
		MutationState state = processInventoryUpdate(warehouseDemands, binDemands);
		Long transactionId = createTransactionAndPersist(request.sourceOrderId(), request.transactionType(),
				request.operatorId(), request.remark(), request.linkedOrderIds(), state);
		for (OutBounding item : items) {
			Reservation reservation = repository.queryReservations(ReservationQueryOptions.byId(item.reservationId()))
					.stream()
					.findFirst()
					.orElseThrow(() -> new InventoryException("reservation not found: " + item.reservationId()));
			if (!reservation.skuId().equals(item.skuId()) || !reservation.binId().equals(item.binId())
					|| reservation.remaining() < item.quantity()) {
				throw InventoryException.insufficientBinStock(item.skuId(), item.binId(), item.quantity(),
						reservation.remaining());
			}
			repository.consumeReservation(item.reservationId(), item.quantity(), transactionId);
		}
		Map<BalanceKey, Quantity> warehouseLedgers = request.transactionType() == TransactionType.OutTransfer
				? withoutInTransitOnly(warehouseDemands) : warehouseDemands;
		saveLedgers(warehouseLedgers, binDemands, state.updatedBalances(), state.updatedBinBalances(),
				request.transactionType(), transactionId, reservationProvider(items), true, null, null, items);
	}

	@Override
	@Transactional
	public void adjust(AdjustRequest request) {
		List<com.commerce.inventory.domain.model.InventoryModels.Adjusting> items = safeList(request.items());
		if (items.isEmpty()) {
			return;
		}
		List<Long> binIds = items.stream().flatMap(item -> java.util.stream.Stream.of(item.fromBinId(), item.toBinId()))
				.distinct().toList();
		Map<Long, BinInfo> binInfos = validateBins(binIds, true);
		List<WarehouseDelta> warehouseDeltas = new ArrayList<>();
		List<BinDelta> binDeltas = new ArrayList<>();
		for (var item : items) {
			BinInfo from = binInfos.get(item.fromBinId());
			BinInfo to = binInfos.get(item.toBinId());
			if (!from.warehouseId().equals(to.warehouseId())) {
				throw new InventoryException("adjust requires same warehouse bins");
			}
			warehouseDeltas.add(new WarehouseDelta(item.skuId(), from.warehouseId(),
					from.binType().outboundDelta(item.quantity(), false).combine(to.binType().inboundDelta(item.quantity()))));
			binDeltas.add(new BinDelta(item.skuId(), item.fromBinId(), BinQuantity.quantityOnly(-item.quantity())));
			binDeltas.add(new BinDelta(item.skuId(), item.toBinId(), BinQuantity.quantityOnly(item.quantity())));
		}
		MutationState state = processInventoryUpdate(aggregateWarehouse(warehouseDeltas), aggregateBin(binDeltas));
		Long transactionId = createTransactionAndPersist(request.sourceOrderId(), request.transactionType(),
				request.operatorId(), request.remark(), request.linkedOrderIds(), state);
		saveLedgers(state.warehouseDemands(), state.binDemands(), state.updatedBalances(), state.updatedBinBalances(),
				request.transactionType(), transactionId, null, true, null, null);
	}

	@Override
	@Transactional
	public void adjustLocked(AdjustLockedRequest request) {
		List<OutBounding> items = safeList(request.items());
		if (items.isEmpty()) {
			return;
		}
		Map<Long, Long> targetBinByReservationId = request.targetBinByReservationId() == null
				? Map.of() : request.targetBinByReservationId();
		List<Long> binIds = new ArrayList<>(items.stream().map(OutBounding::binId).toList());
		binIds.addAll(targetBinByReservationId.values());
		Map<Long, BinInfo> binInfos = validateBins(binIds.stream().distinct().toList(), true);
		List<WarehouseDelta> warehouseDeltas = new ArrayList<>();
		List<BinDelta> binDeltas = new ArrayList<>();
		for (OutBounding item : items) {
			Long targetBinId = targetBinByReservationId.get(item.reservationId());
			Reservation reservation = repository.queryReservations(ReservationQueryOptions.byId(item.reservationId()))
					.stream()
					.findFirst()
					.orElseThrow(() -> InventoryException.versionConflict(request.sourceOrderId()));
			if (!reservation.skuId().equals(item.skuId()) || !reservation.binId().equals(item.binId())
					|| reservation.remaining() != item.quantity() || reservation.cancelledTime() != null) {
				throw InventoryException.versionConflict(request.sourceOrderId());
			}
			BinInfo from = binInfos.get(item.binId());
			BinInfo to = binInfos.get(targetBinId);
			if (!from.warehouseId().equals(to.warehouseId())) {
				throw InventoryException.versionConflict(request.sourceOrderId());
			}
			warehouseDeltas.add(new WarehouseDelta(item.skuId(), from.warehouseId(),
					from.binType().outboundDelta(item.quantity()).combine(to.binType().inboundDelta(item.quantity()))));
			binDeltas.add(new BinDelta(item.skuId(), item.binId(), new BinQuantity(-item.quantity(), -item.quantity())));
			binDeltas.add(new BinDelta(item.skuId(), targetBinId, BinQuantity.quantityOnly(item.quantity())));
		}
		MutationState state = processInventoryUpdate(aggregateWarehouse(warehouseDeltas), aggregateBin(binDeltas));
		Long transactionId = createTransactionAndPersist(request.sourceOrderId(), request.transactionType(),
				request.operatorId(), request.remark(), request.linkedOrderIds(), state);
		for (OutBounding item : items) {
			repository.consumeReservation(item.reservationId(), item.quantity(), transactionId);
		}
		saveLedgers(state.warehouseDemands(), state.binDemands(), state.updatedBalances(), state.updatedBinBalances(),
				request.transactionType(), transactionId, reservationProvider(items), false, request.sourceBinLedgerType(),
				request.targetBinLedgerType(), null, items, targetBinByReservationId);
	}

	@Override
	@Transactional
	public List<Long> lock(LockRequest request) {
		List<Locking> items = safeList(request.items());
		if (items.isEmpty()) {
			return List.of();
		}
		Map<Long, BinInfo> binInfos = repository.queryBinInfos(
				BinQueryOptions.byIds(items.stream().map(Locking::binId).distinct().toList()));
		List<WarehouseDelta> warehouseDeltas = new ArrayList<>();
		List<BinDelta> binDeltas = new ArrayList<>();
		for (Locking item : items) {
			BinInfo binInfo = binInfos.get(item.binId());
			if (binInfo == null) {
				throw InventoryException.insufficientBinStock(item.skuId(), item.binId(), item.quantity(), 0);
			}
			warehouseDeltas.add(new WarehouseDelta(item.skuId(), item.warehouseId(), binInfo.binType().lockDelta(item.quantity())));
			binDeltas.add(new BinDelta(item.skuId(), item.binId(), BinQuantity.lockOnly(item.quantity())));
		}
		MutationState state = processInventoryUpdate(aggregateWarehouse(warehouseDeltas), aggregateBin(binDeltas));
		Long transactionId = createTransactionAndPersist(request.sourceOrderId(), request.transactionType(),
				request.operatorId(), request.remark(), request.linkedOrderIds(), state);
		List<ReservationCreate> reservations = items.stream()
				.map(item -> new ReservationCreate(item.skuId(), item.warehouseId(), item.binId(), null, transactionId,
						item.quantity(), null, null, request.remark()))
				.toList();
		List<Long> ids = repository.createReservations(reservations);
		Map<BinBalanceKey, Long> reservationByKey = new HashMap<>();
		for (int i = 0; i < items.size(); i++) {
			reservationByKey.putIfAbsent(new BinBalanceKey(items.get(i).skuId(), items.get(i).binId()), ids.get(i));
		}
		saveLedgers(state.warehouseDemands(), state.binDemands(), state.updatedBalances(), state.updatedBinBalances(),
				request.transactionType(), transactionId, key -> reservationByKey.get((BinBalanceKey) key), true, null,
				request.ledgerRemark());
		return ids;
	}

	@Override
	@Transactional
	public void unlock(UnlockRequest request) {
		List<OutBounding> items = safeList(request.items());
		if (items.isEmpty()) {
			return;
		}
		Map<Long, BinInfo> binInfos = validateBins(items.stream().map(OutBounding::binId).distinct().toList(), false);
		List<WarehouseDelta> warehouseDeltas = new ArrayList<>();
		List<BinDelta> binDeltas = new ArrayList<>();
		for (OutBounding item : items) {
			Reservation reservation = repository.queryReservations(ReservationQueryOptions.byId(item.reservationId()))
					.stream()
					.findFirst()
					.orElseThrow(() -> InventoryException.versionConflict(request.sourceOrderId()));
			if (!reservation.skuId().equals(item.skuId()) || !reservation.binId().equals(item.binId())
					|| reservation.remaining() != item.quantity() || reservation.cancelledTime() != null) {
				throw InventoryException.versionConflict(request.sourceOrderId());
			}
			BinInfo binInfo = binInfos.get(item.binId());
			Quantity delta = binInfo.binType().outboundDelta(item.quantity()).combine(binInfo.binType().inboundDelta(item.quantity()));
			warehouseDeltas.add(new WarehouseDelta(item.skuId(), binInfo.warehouseId(), delta));
			binDeltas.add(new BinDelta(item.skuId(), item.binId(), BinQuantity.lockOnly(-item.quantity())));
		}
		MutationState state = processInventoryUpdate(aggregateWarehouse(warehouseDeltas), aggregateBin(binDeltas));
		Long transactionId = createTransactionAndPersist(request.sourceOrderId(), request.transactionType(),
				request.operatorId(), request.remark(), request.linkedOrderIds(), state);
		for (OutBounding item : items) {
			repository.consumeReservation(item.reservationId(), item.quantity(), transactionId);
		}
		saveLedgers(state.warehouseDemands(), state.binDemands(), state.updatedBalances(), state.updatedBinBalances(),
				request.transactionType(), transactionId, reservationProvider(items), true, null, request.ledgerRemark(), items);
		if (request.cancelReservation()) {
			repository.cancelReservations(items.stream().map(OutBounding::reservationId).distinct().toList());
		}
	}

	@Override
	public Optional<Balance> getBalance(Long skuId, Long warehouseId) {
		return repository.queryBalances(BalanceQueryOptions.bySkuAndWarehouse(skuId, warehouseId)).stream().findFirst();
	}

	@Override
	public Optional<BinBalance> getBinBalance(Long skuId, Long binId) {
		return repository.queryBinBalances(BinBalanceQueryOptions.bySkuAndBin(skuId, binId)).stream().findFirst();
	}

	@Override
	@Transactional
	public Long createWarehouse(CreateWarehouseRequest request) {
		return repository.create(request);
	}

	@Override
	@Transactional
	public Long createBin(CreateBinRequest request) {
		return repository.create(request);
	}

	@Override
	public List<Warehouse> listWarehouses() {
		return repository.queryWarehouses(WarehouseQueryOptions.none());
	}

	@Override
	public List<Bin> listBins(Long warehouseId) {
		return repository.queryBins(BinQueryOptions.byWarehouseId(warehouseId));
	}

	@Override
	@Transactional
	public InventoryReceipt createReceipt(CreateReceiptRequest request) {
		return receiptService.create(request);
	}

	@Override
	@Transactional
	public InventoryReceipt transitReceipt(TransitReceiptRequest request) {
		return receiptService.transit(request);
	}

	@Override
	public Optional<InventoryReceipt> getReceipt(Long receiptId) {
		return receiptService.get(receiptId);
	}

	@Override
	public PageResponse<InventoryReceipt> listReceipts(String typeKey, int pageNumber, int pageSize) {
		return receiptService.list(typeKey, pageNumber, pageSize);
	}

	private Map<Long, BinInfo> validateBins(List<Long> binIds, boolean enabledOnly) {
		if (binIds == null || binIds.isEmpty()) {
			return Map.of();
		}
		Map<Long, BinInfo> binInfos = repository.queryBinInfos(BinQueryOptions.byIds(binIds));
		if (binInfos.size() != binIds.size()) {
			throw new InventoryException("bin not found");
		}
		if (enabledOnly) {
			binInfos.forEach((binId, info) -> {
				if (info.disable()) {
					throw new InventoryException("bin disabled: " + binId);
				}
			});
		}
		return binInfos;
	}

	private MutationState processInventoryUpdate(Map<BalanceKey, Quantity> warehouseDemands,
			Map<BinBalanceKey, BinQuantity> binDemands) {
		Map<BalanceKey, Balance> existingBalances = repository.queryBalancesForUpdate(
				new ArrayList<>(warehouseDemands.keySet()));
		Map<BinBalanceKey, BinBalance> existingBinBalances = repository.queryBinBalancesForUpdate(
				new ArrayList<>(binDemands.keySet()));
		Map<BalanceKey, Balance> updatedBalances = new LinkedHashMap<>();
		for (Map.Entry<BalanceKey, Quantity> entry : warehouseDemands.entrySet()) {
			BalanceKey key = entry.getKey();
			Quantity delta = entry.getValue();
			Balance existing = existingBalances.get(key);
			Quantity updated = existing == null ? delta : existing.quantity().combine(delta);
			if (!updated.isNonNegative()) {
				throw InventoryException.insufficientStock(key.skuId(), key.warehouseId(), 0, updated.available());
			}
			updatedBalances.put(key, new Balance(existing == null ? null : existing.id(), key.skuId(), key.warehouseId(),
					updated.available(), updated.availableLock(), updated.defective(), updated.defectiveLock(),
					updated.toInspection(), updated.transferInTransit(), existing == null ? ZonedDateTime.now() : existing.createdTime(),
					ZonedDateTime.now()));
		}
		Map<BinBalanceKey, BinBalance> updatedBinBalances = new LinkedHashMap<>();
		for (Map.Entry<BinBalanceKey, BinQuantity> entry : binDemands.entrySet()) {
			BinBalanceKey key = entry.getKey();
			BinQuantity delta = entry.getValue();
			BinBalance existing = existingBinBalances.get(key);
			long quantity = (existing == null ? 0 : existing.quantity()) + delta.quantity();
			long lockQty = (existing == null ? 0 : existing.lockQty()) + delta.lockQty();
			if (quantity < 0 || lockQty < 0 || lockQty > quantity) {
				throw InventoryException.insufficientBinStock(key.skuId(), key.binId(), Math.max(lockQty, quantity), quantity);
			}
			updatedBinBalances.put(key, new BinBalance(existing == null ? null : existing.id(), key.skuId(), key.binId(),
					quantity, lockQty, existing == null ? ZonedDateTime.now() : existing.createdTime(), ZonedDateTime.now()));
		}
		return new MutationState(warehouseDemands, binDemands, existingBalances, existingBinBalances, updatedBalances,
				updatedBinBalances);
	}

	private Long createTransactionAndPersist(Long sourceOrderId, TransactionType type, Long operatorId, String remark,
			List<Long> linkedOrderIds, MutationState state) {
		Long transactionId = repository.createTransaction(sourceOrderId, type, operatorId, remark, safeList(linkedOrderIds));
		state.updatedBalances().forEach((key, balance) -> repository.saveOrUpdateBalance(balance,
				state.existingBalances().containsKey(key)));
		state.updatedBinBalances().forEach((key, balance) -> repository.saveOrUpdateBinBalance(balance,
				state.existingBinBalances().containsKey(key)));
		return transactionId;
	}

	private void saveLedgers(Map<BalanceKey, Quantity> warehouseDemands, Map<BinBalanceKey, BinQuantity> binDemands,
			Map<BalanceKey, Balance> updatedBalances, Map<BinBalanceKey, BinBalance> updatedBinBalances,
			TransactionType transactionType, Long transactionId, ReservationProvider reservationProvider,
			boolean includeWarehouseLedgers, BinLedgerType overrideBinLedgerType, String ledgerRemark) {
		saveLedgers(warehouseDemands, binDemands, updatedBalances, updatedBinBalances, transactionType, transactionId,
				reservationProvider, includeWarehouseLedgers, overrideBinLedgerType, ledgerRemark, List.of());
	}

	private void saveLedgers(Map<BalanceKey, Quantity> warehouseDemands, Map<BinBalanceKey, BinQuantity> binDemands,
			Map<BalanceKey, Balance> updatedBalances, Map<BinBalanceKey, BinBalance> updatedBinBalances,
			TransactionType transactionType, Long transactionId, ReservationProvider reservationProvider,
			boolean includeWarehouseLedgers, BinLedgerType overrideBinLedgerType, String ledgerRemark,
			List<OutBounding> sourceItems) {
		saveLedgers(warehouseDemands, binDemands, updatedBalances, updatedBinBalances, transactionType, transactionId,
				reservationProvider, includeWarehouseLedgers, overrideBinLedgerType, null, ledgerRemark, sourceItems, Map.of());
	}

	private void saveLedgers(Map<BalanceKey, Quantity> warehouseDemands, Map<BinBalanceKey, BinQuantity> binDemands,
			Map<BalanceKey, Balance> updatedBalances, Map<BinBalanceKey, BinBalance> updatedBinBalances,
			TransactionType transactionType, Long transactionId, ReservationProvider reservationProvider,
			boolean includeWarehouseLedgers, BinLedgerType sourceBinLedgerType, BinLedgerType targetBinLedgerType,
			String ledgerRemark, List<OutBounding> sourceItems, Map<Long, Long> targetBinByReservationId) {
		if (includeWarehouseLedgers) {
			WarehouseLedgerType warehouseLedgerType = warehouseLedgerType(transactionType);
			if (warehouseLedgerType != null) {
				warehouseDemands.forEach((key, delta) -> {
					if (delta.hasChange()) {
						repository.saveWarehouseLedger(key.skuId(), key.warehouseId(), warehouseLedgerType, delta,
								updatedBalances.get(key), transactionId, ledgerRemark);
					}
				});
			}
		}
		BinLedgerType defaultBinLedgerType = binLedgerType(transactionType);
		binDemands.forEach((key, delta) -> {
			BinLedgerType ledgerType = defaultBinLedgerType;
			if (sourceBinLedgerType != null || targetBinLedgerType != null) {
				ledgerType = sourceItems.stream().filter(item -> item.skuId().equals(key.skuId()) && item.binId().equals(key.binId()))
						.findFirst().map(item -> sourceBinLedgerType).orElseGet(() -> sourceItems.stream()
								.filter(item -> item.skuId().equals(key.skuId())
										&& Objects.equals(targetBinByReservationId.get(item.reservationId()), key.binId()))
								.findFirst().map(item -> targetBinLedgerType).orElse(null));
			}
			if (ledgerType != null && (delta.quantity() != 0 || delta.lockQty() != 0)) {
				Long reservationId = reservationProvider == null ? null : reservationProvider.reservationId(key);
				repository.saveBinLedger(key.skuId(), key.binId(), ledgerType, delta, updatedBinBalances.get(key),
						reservationId, transactionId, ledgerRemark);
			}
		});
	}

	private ReservationProvider reservationProvider(List<OutBounding> items) {
		return key -> {
			BinBalanceKey binKey = (BinBalanceKey) key;
			return items.stream()
					.filter(item -> item.skuId().equals(binKey.skuId()) && item.binId().equals(binKey.binId()))
					.map(OutBounding::reservationId)
					.findFirst()
					.orElse(null);
		};
	}

	private static WarehouseLedgerType warehouseLedgerType(TransactionType transactionType) {
		if (transactionType == TransactionType.LocationTransfer) {
			return null;
		}
		return WarehouseLedgerType.valueOf(transactionType.name());
	}

	private static BinLedgerType binLedgerType(TransactionType transactionType) {
		if (transactionType == TransactionType.StockAdjustment || transactionType == TransactionType.LocationTransfer) {
			return null;
		}
		return BinLedgerType.valueOf(transactionType.name());
	}

	private static Map<BalanceKey, Quantity> withoutInTransitOnly(Map<BalanceKey, Quantity> demands) {
		Map<BalanceKey, Quantity> result = new LinkedHashMap<>();
		demands.forEach((key, quantity) -> {
			boolean inTransitOnly = quantity.transferInTransit() != 0 && quantity.available() == 0
					&& quantity.availableLock() == 0 && quantity.defective() == 0 && quantity.defectiveLock() == 0
					&& quantity.toInspection() == 0;
			if (!inTransitOnly) {
				result.put(key, quantity);
			}
		});
		return result;
	}

	private static Map<BalanceKey, Quantity> aggregateWarehouse(List<WarehouseDelta> deltas) {
		Map<BalanceKey, Quantity> result = new LinkedHashMap<>();
		deltas.stream().sorted(Comparator.comparing(WarehouseDelta::skuId).thenComparing(WarehouseDelta::warehouseId))
				.forEach(delta -> result.merge(new BalanceKey(delta.skuId(), delta.warehouseId()), delta.delta(),
						Quantity::combine));
		return result;
	}

	private static Map<BinBalanceKey, BinQuantity> aggregateBin(List<BinDelta> deltas) {
		Map<BinBalanceKey, BinQuantity> result = new LinkedHashMap<>();
		deltas.stream().sorted(Comparator.comparing(BinDelta::skuId).thenComparing(BinDelta::binId))
				.forEach(delta -> result.merge(new BinBalanceKey(delta.skuId(), delta.binId()), delta.delta(),
						BinQuantity::combine));
		return result;
	}

	private static <T> List<T> safeList(List<T> items) {
		return items == null ? List.of() : items;
	}

	private interface ReservationProvider {
		Long reservationId(Object key);
	}

	private record WarehouseDelta(Long skuId, Long warehouseId, Quantity delta) {}

	private record BinDelta(Long skuId, Long binId, BinQuantity delta) {}

	private record MutationState(
			Map<BalanceKey, Quantity> warehouseDemands,
			Map<BinBalanceKey, BinQuantity> binDemands,
			Map<BalanceKey, Balance> existingBalances,
			Map<BinBalanceKey, BinBalance> existingBinBalances,
			Map<BalanceKey, Balance> updatedBalances,
			Map<BinBalanceKey, BinBalance> updatedBinBalances
	) {}

}

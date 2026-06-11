package com.commerce.inventory.application.service;

import com.commerce.inventory.application.port.InventoryUseCase;
import com.commerce.inventory.domain.model.BinQueryOptions;
import com.commerce.inventory.domain.model.InventoryException;
import com.commerce.inventory.domain.model.InventoryModels.AdjustLockedRequest;
import com.commerce.inventory.domain.model.InventoryModels.Bin;
import com.commerce.inventory.domain.model.InventoryModels.BinBalance;
import com.commerce.inventory.domain.model.InventoryModels.BinLedgerType;
import com.commerce.inventory.domain.model.InventoryModels.BinType;
import com.commerce.inventory.domain.model.InventoryModels.CreateReceiptRequest;
import com.commerce.inventory.domain.model.InventoryModels.InBounding;
import com.commerce.inventory.domain.model.InventoryModels.InboundItem;
import com.commerce.inventory.domain.model.InventoryModels.InboundPayload;
import com.commerce.inventory.domain.model.InventoryModels.InventoryReceipt;
import com.commerce.inventory.domain.model.InventoryModels.LocationTransferPayload;
import com.commerce.inventory.domain.model.InventoryModels.LockRequest;
import com.commerce.inventory.domain.model.InventoryModels.Locking;
import com.commerce.inventory.domain.model.InventoryModels.NumberType;
import com.commerce.inventory.domain.model.InventoryModels.OutBounding;
import com.commerce.inventory.domain.model.InventoryModels.OutboundBinAllocation;
import com.commerce.inventory.domain.model.InventoryModels.OutboundItem;
import com.commerce.inventory.domain.model.InventoryModels.OutboundPayload;
import com.commerce.inventory.domain.model.InventoryModels.OutboundRequest;
import com.commerce.inventory.domain.model.InventoryModels.PageResponse;
import com.commerce.inventory.domain.model.InventoryModels.ReceiptEvent;
import com.commerce.inventory.domain.model.InventoryModels.ReceiptType;
import com.commerce.inventory.domain.model.InventoryModels.Reservation;
import com.commerce.inventory.domain.model.InventoryModels.TransactionType;
import com.commerce.inventory.domain.model.InventoryModels.TransferItem;
import com.commerce.inventory.domain.model.InventoryModels.TransferPayload;
import com.commerce.inventory.domain.model.InventoryModels.TransitReceiptRequest;
import com.commerce.inventory.domain.model.InventoryModels.UnlockRequest;
import com.commerce.inventory.domain.model.ReservationQueryOptions;
import com.commerce.inventory.infrastructure.persistence.MyBatisInventoryRepository;
import com.commerce.inventory.infrastructure.persistence.MyBatisInventoryRepository.BinBalanceKey;
import com.commerce.inventory.infrastructure.persistence.MyBatisInventoryRepository.ReceiptRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class InventoryReceiptService {

	private static final String AVAILABLE_TEMP_BIN = "可用暂存";
	private static final String INSPECTION_TEMP_BIN = "待检暂存";
	private static final String DEFECTIVE_TEMP_BIN = "次品暂存";

	private final MyBatisInventoryRepository repository;
	private final ObjectMapper objectMapper;
	private final InventoryUseCase inventoryUseCase;

	public InventoryReceiptService(
			MyBatisInventoryRepository repository,
			ObjectMapper objectMapper,
			@Lazy InventoryUseCase inventoryUseCase
	) {
		this.repository = repository;
		this.objectMapper = objectMapper;
		this.inventoryUseCase = inventoryUseCase;
	}

	public InventoryReceipt create(CreateReceiptRequest request) {
		ReceiptType type = request.typeKey();
		JsonNode payload = requirePayload(request.payload());
		validatePayload(type, payload);
		String number = request.number() == null || request.number().isBlank()
				? repository.nextReceiptNumber(numberType(type)) : request.number();
		Long receiptId = repository.createReceipt(number, type.name(), request.operatorId());
		String initialState = request.initialState() == null || request.initialState().isBlank()
				? initialState(type) : request.initialState();
		repository.createReceiptRecord(receiptId, initialState, payload, request.operatorId());
		return get(receiptId).orElseThrow();
	}

	public InventoryReceipt transit(TransitReceiptRequest request) {
		ReceiptRow current = repository.queryReceipt(request.receiptId())
				.orElseThrow(() -> new InventoryException("receipt not found: " + request.receiptId()));
		ReceiptType type = ReceiptType.valueOf(current.typeKey());
		Transition transition = transition(type, current.state(), request.event());
		JsonNode payload = executeAction(type, request.event(), current, request.payload(), request.operatorId());
		repository.createReceiptRecord(request.receiptId(), transition.toState(), payload, request.operatorId());
		return get(request.receiptId()).orElseThrow();
	}

	public Optional<InventoryReceipt> get(Long receiptId) {
		return repository.queryReceipt(receiptId).map(this::toReceipt);
	}

	public PageResponse<InventoryReceipt> list(String typeKey, int pageNumber, int pageSize) {
		int normalizedPage = Math.max(pageNumber, 1);
		int normalizedSize = Math.max(pageSize, 1);
		List<InventoryReceipt> data = repository.listReceipts(typeKey, normalizedPage, normalizedSize).stream()
				.map(this::toReceipt)
				.toList();
		return new PageResponse<>(data, normalizedPage, normalizedSize, repository.countReceipts(typeKey));
	}

	private JsonNode executeAction(ReceiptType type, ReceiptEvent event, ReceiptRow current, JsonNode incomingPayload,
			Long operatorId) {
		return switch (type) {
			case INBOUND -> inboundAction(event, current, incomingPayload, operatorId);
			case OUTBOUND -> outboundAction(event, current, incomingPayload, operatorId);
			case TRANSFER -> transferAction(event, current, incomingPayload, operatorId);
			case LOCATION_TRANSFER -> locationTransferAction(event, current, incomingPayload, operatorId);
		};
	}

	private JsonNode inboundAction(ReceiptEvent event, ReceiptRow current, JsonNode incomingPayload, Long operatorId) {
		return switch (event) {
			case Reserve, Cancel -> requirePayload(incomingPayload);
			case Submit -> valueToTree(fillInboundBins(readPayload(requirePayload(incomingPayload), InboundPayload.class)));
			case ConfirmInbound -> {
				InboundPayload payload = readPayload(current.payload(), InboundPayload.class);
				List<InBounding> items = payload.items().stream().flatMap(item -> inboundOps(item).stream()).toList();
				inventoryUseCase.inbound(new com.commerce.inventory.domain.model.InventoryModels.InboundRequest(
						payload.inBoundingType(), items, current.id(), operatorId, payload.remark(),
						nullableList(payload.linkedTransferReceiptId())
				));
				yield valueToTree(new InboundPayload(payload.inBoundingType(), payload.warehouseId(), payload.supplierId(),
						payload.purchaserId(), payload.items(), payload.remark(), payload.linkedTransferReceiptId(),
						operatorId, ZonedDateTime.now()));
			}
			case Rollback -> current.payload();
			default -> throw new InventoryException("unsupported inbound event: " + event);
		};
	}

	private JsonNode outboundAction(ReceiptEvent event, ReceiptRow current, JsonNode incomingPayload, Long operatorId) {
		return switch (event) {
			case Reserve -> requirePayload(incomingPayload);
			case Submit -> {
				OutboundPayload payload = readPayload(requirePayload(incomingPayload), OutboundPayload.class);
				LockPlan lockPlan = buildLockPlan(payload, current.id(), operatorId);
				List<Long> reservationIds = inventoryUseCase.lock(lockPlan.lockRequest());
				List<OutboundItem> savedItems = applyReservations(payload.items(), lockPlan.lockItemsByItem(), reservationIds);
				yield valueToTree(new OutboundPayload(payload.outBoundingType(), payload.warehouseId(), savedItems,
						payload.linkedTransferReceiptId(), payload.remark(), operatorId, ZonedDateTime.now()));
			}
			case ConfirmOutbound -> {
				OutboundPayload payload = readPayload(current.payload(), OutboundPayload.class);
				inventoryUseCase.outbound(buildOutboundRequest(payload, current.id(), operatorId));
				yield valueToTree(new OutboundPayload(payload.outBoundingType(), payload.warehouseId(), payload.items(),
						payload.linkedTransferReceiptId(), payload.remark(), operatorId, ZonedDateTime.now()));
			}
			case Rollback -> {
				OutboundPayload payload = readPayload(current.payload(), OutboundPayload.class);
				if (payload.outBoundingType() == TransactionType.OutTransfer) {
					throw new InventoryException("outbound rollback is not allowed for transfer outbound");
				}
				unlockReservations(payload, current.id(), operatorId);
				yield current.payload();
			}
			case Cancel -> current.payload();
			default -> throw new InventoryException("unsupported outbound event: " + event);
		};
	}

	private JsonNode transferAction(ReceiptEvent event, ReceiptRow current, JsonNode incomingPayload, Long operatorId) {
		return switch (event) {
			case Reserve, Cancel -> requirePayload(incomingPayload);
			case Submit -> {
				TransferPayload payload = readPayload(requirePayload(incomingPayload), TransferPayload.class);
				OutboundPayload outboundPayload = pendingOutboundPayload(payload, current.id());
				InventoryReceipt outbound = create(new CreateReceiptRequest(
						ReceiptType.OUTBOUND, valueToTree(outboundPayload), operatorId, null, null));
				transit(new TransitReceiptRequest(outbound.id(), ReceiptEvent.Submit, valueToTree(outboundPayload), operatorId));
				yield valueToTree(new TransferPayload(payload.items(), payload.sourceWarehouseId(), payload.targetWarehouseId(),
						payload.remark(), outbound.id(), payload.linkedInboundReceiptId(), operatorId, ZonedDateTime.now(),
						payload.outboundTime(), payload.inboundTime()));
			}
			case ConfirmTransfer -> {
				TransferPayload payload = readPayload(current.payload(), TransferPayload.class);
				if (payload.linkedOutboundReceiptId() == null) {
					throw new InventoryException("linked outbound receipt is required");
				}
				transit(new TransitReceiptRequest(payload.linkedOutboundReceiptId(), ReceiptEvent.ConfirmOutbound, null,
						operatorId));
				yield valueToTree(new TransferPayload(payload.items(), payload.sourceWarehouseId(), payload.targetWarehouseId(),
						payload.remark(), payload.linkedOutboundReceiptId(), payload.linkedInboundReceiptId(), operatorId,
						payload.submitTime(), ZonedDateTime.now(), payload.inboundTime()));
			}
			case ConfirmTransferInbound -> {
				TransferPayload payload = readPayload(current.payload(), TransferPayload.class);
				InboundPayload inboundPayload = pendingInboundPayload(current.id(), payload);
				InventoryReceipt inbound = create(new CreateReceiptRequest(
						ReceiptType.INBOUND, valueToTree(inboundPayload), operatorId, null, null));
				transit(new TransitReceiptRequest(inbound.id(), ReceiptEvent.Submit, valueToTree(inboundPayload), operatorId));
				transit(new TransitReceiptRequest(inbound.id(), ReceiptEvent.ConfirmInbound, null, operatorId));
				yield valueToTree(new TransferPayload(payload.items(), payload.sourceWarehouseId(), payload.targetWarehouseId(),
						payload.remark(), payload.linkedOutboundReceiptId(), inbound.id(), operatorId, payload.submitTime(),
						payload.outboundTime(), ZonedDateTime.now()));
			}
			case Rollback -> {
				TransferPayload payload = readPayload(current.payload(), TransferPayload.class);
				if (payload.linkedOutboundReceiptId() != null) {
					transit(new TransitReceiptRequest(payload.linkedOutboundReceiptId(), ReceiptEvent.Rollback, null,
							operatorId));
				}
				yield current.payload();
			}
			default -> throw new InventoryException("unsupported transfer event: " + event);
		};
	}

	private JsonNode locationTransferAction(ReceiptEvent event, ReceiptRow current, JsonNode incomingPayload,
			Long operatorId) {
		return switch (event) {
			case Submit -> {
				LocationTransferPayload payload = readPayload(requirePayload(incomingPayload), LocationTransferPayload.class);
				validateLocationTransferBins(payload);
				List<Long> reservationIds = inventoryUseCase.lock(new LockRequest(TransactionType.StockAdjustment,
						List.of(new Locking(payload.skuId(), payload.sourceBinId(), payload.warehouseId(),
								payload.quantity())),
						current.id(), operatorId, null, null, List.of()));
				yield valueToTree(new LocationTransferPayload(payload.skuId(), payload.quantity(), payload.warehouseId(),
						payload.sourceBinId(), payload.targetBinId(), reservationIds.get(0), operatorId, ZonedDateTime.now(),
						payload.locationTransferTime()));
			}
			case ConfirmLocationTransfer -> {
				LocationTransferPayload payload = readPayload(current.payload(), LocationTransferPayload.class);
				if (payload.reservationId() == null) {
					throw new InventoryException("location transfer reservation is required");
				}
				inventoryUseCase.adjustLocked(new AdjustLockedRequest(TransactionType.LocationTransfer,
						List.of(new OutBounding(payload.skuId(), payload.sourceBinId(), payload.quantity(),
								payload.reservationId(), null)),
						Map.of(payload.reservationId(), payload.targetBinId()), BinLedgerType.RelocatePicking,
						BinLedgerType.RelocatePutaway, current.id(), operatorId, null, List.of()));
				yield valueToTree(new LocationTransferPayload(payload.skuId(), payload.quantity(), payload.warehouseId(),
						payload.sourceBinId(), payload.targetBinId(), null, operatorId, payload.submitTime(),
						ZonedDateTime.now()));
			}
			case Cancel -> {
				LocationTransferPayload payload = readPayload(current.payload(), LocationTransferPayload.class);
				if (payload.reservationId() != null) {
					inventoryUseCase.unlock(new UnlockRequest(TransactionType.StockAdjustment,
							List.of(new OutBounding(payload.skuId(), payload.sourceBinId(), payload.quantity(),
									payload.reservationId(), null)),
							current.id(), operatorId, null, null, true, List.of()));
				}
				yield current.payload();
			}
			default -> throw new InventoryException("unsupported location transfer event: " + event);
		};
	}

	private InboundPayload fillInboundBins(InboundPayload payload) {
		List<Bin> bins = repository.queryBins(BinQueryOptions.byWarehouseId(payload.warehouseId()));
		Long availableBin = binIdByName(bins, AVAILABLE_TEMP_BIN);
		Long inspectionBin = binIdByName(bins, INSPECTION_TEMP_BIN);
		Long defectiveBin = binIdByName(bins, DEFECTIVE_TEMP_BIN);
		List<InboundItem> items = payload.items().stream().map(item -> new InboundItem(
				item.skuId(),
				item.availableQty(),
				resolveBin(item.availableQty(), item.availableBinId(), availableBin, "available"),
				item.inspectionQty(),
				resolveBin(item.inspectionQty(), item.inspectionBinId(), inspectionBin, "inspection"),
				item.defectiveQty(),
				resolveBin(item.defectiveQty(), item.defectiveBinId(), defectiveBin, "defective"),
				item.remark()
		)).toList();
		return new InboundPayload(payload.inBoundingType(), payload.warehouseId(), payload.supplierId(),
				payload.purchaserId(), items, payload.remark(), payload.linkedTransferReceiptId(), payload.operatorId(),
				payload.submitTime());
	}

	private LockPlan buildLockPlan(OutboundPayload payload, Long receiptId, Long operatorId) {
		List<Bin> bins = repository.queryBins(BinQueryOptions.byWarehouseId(payload.warehouseId()));
		List<List<Locking>> lockItemsByItem = new ArrayList<>();
		for (OutboundItem item : payload.items()) {
			List<Locking> locks = new ArrayList<>();
			locks.addAll(planLocksForPart(item.skuId(), item.availableQty(), payload.warehouseId(), bins, BinType.Available));
			locks.addAll(planLocksForPart(item.skuId(), item.defectiveQty(), payload.warehouseId(), bins, BinType.Defective));
			lockItemsByItem.add(locks);
		}
		LockRequest request = new LockRequest(TransactionType.StockAdjustment, lockItemsByItem.stream().flatMap(List::stream).toList(),
				receiptId, operatorId, payload.remark(), outLockRemark(payload.outBoundingType()),
				nullableList(payload.linkedTransferReceiptId()));
		return new LockPlan(request, lockItemsByItem);
	}

	private List<Locking> planLocksForPart(Long skuId, int quantity, Long warehouseId, List<Bin> bins, BinType binType) {
		if (quantity <= 0) {
			return List.of();
		}
		List<Long> binIds = bins.stream().filter(bin -> bin.binType() == binType).map(Bin::id).toList();
		Map<BinBalanceKey, BinBalance> balances = repository.queryBinBalances(
				binIds.stream().map(binId -> new BinBalanceKey(skuId, binId)).toList());
		List<Bin> sortedBins = bins.stream().filter(bin -> bin.binType() == binType)
				.sorted(Comparator.comparingLong(bin -> availableQty(balances.get(new BinBalanceKey(skuId, bin.id())))))
				.toList();
		long remaining = quantity;
		List<Locking> locks = new ArrayList<>();
		for (Bin bin : sortedBins) {
			if (remaining <= 0) {
				break;
			}
			long available = Math.max(availableQty(balances.get(new BinBalanceKey(skuId, bin.id()))), 0);
			if (available <= 0) {
				continue;
			}
			long locked = Math.min(remaining, available);
			locks.add(new Locking(skuId, bin.id(), warehouseId, locked));
			remaining -= locked;
		}
		if (remaining > 0) {
			throw InventoryException.insufficientStock(skuId, warehouseId, quantity, quantity - remaining);
		}
		return locks;
	}

	private OutboundRequest buildOutboundRequest(OutboundPayload payload, Long receiptId, Long operatorId) {
		Long targetWarehouseId = null;
		if (payload.linkedTransferReceiptId() != null) {
			TransferPayload transferPayload = repository.queryReceipt(payload.linkedTransferReceiptId())
					.map(row -> readPayload(row.payload(), TransferPayload.class))
					.orElseThrow(() -> new InventoryException("linked transfer receipt not found"));
			targetWarehouseId = transferPayload.targetWarehouseId();
		}
		List<OutBounding> items = new ArrayList<>();
		for (OutboundItem item : payload.items()) {
			for (Long reservationId : item.reservationIds()) {
				Reservation reservation = repository.queryReservations(ReservationQueryOptions.byId(reservationId)).stream()
						.findFirst()
						.orElseThrow(() -> new InventoryException("reservation not found: " + reservationId));
				items.add(new OutBounding(item.skuId(), reservation.binId(), reservation.remaining(), reservation.id(),
						targetWarehouseId));
			}
		}
		return new OutboundRequest(payload.outBoundingType(), items, receiptId, operatorId, payload.remark(),
				nullableList(payload.linkedTransferReceiptId()));
	}

	private void unlockReservations(OutboundPayload payload, Long receiptId, Long operatorId) {
		List<OutBounding> unlockItems = new ArrayList<>();
		for (OutboundItem item : payload.items()) {
			for (Long reservationId : item.reservationIds()) {
				Reservation reservation = repository.queryReservations(ReservationQueryOptions.byId(reservationId)).stream()
						.findFirst()
						.orElseThrow(() -> new InventoryException("reservation not found: " + reservationId));
				unlockItems.add(new OutBounding(reservation.skuId(), reservation.binId(), reservation.remaining(),
						reservation.id(), null));
			}
		}
		if (!unlockItems.isEmpty()) {
			inventoryUseCase.unlock(new UnlockRequest(TransactionType.StockAdjustment, unlockItems, receiptId, operatorId,
					payload.remark(), "回退释放", true, nullableList(payload.linkedTransferReceiptId())));
		}
	}

	private void validateLocationTransferBins(LocationTransferPayload payload) {
		Map<Long, Bin> bins = new HashMap<>();
		for (Bin bin : repository.queryBins(BinQueryOptions.byIds(List.of(payload.sourceBinId(), payload.targetBinId())))) {
			bins.put(bin.id(), bin);
		}
		Bin source = bins.get(payload.sourceBinId());
		Bin target = bins.get(payload.targetBinId());
		if (source == null || target == null || source.binType() != target.binType()) {
			throw new InventoryException("location transfer source/target bin type mismatch");
		}
	}

	private static List<InBounding> inboundOps(InboundItem item) {
		List<InBounding> ops = new ArrayList<>();
		if (item.availableQty() > 0) {
			ops.add(new InBounding(item.skuId(), item.availableBinId(), item.availableQty()));
		}
		if (item.inspectionQty() > 0) {
			ops.add(new InBounding(item.skuId(), item.inspectionBinId(), item.inspectionQty()));
		}
		if (item.defectiveQty() > 0) {
			ops.add(new InBounding(item.skuId(), item.defectiveBinId(), item.defectiveQty()));
		}
		return ops;
	}

	private static List<OutboundItem> applyReservations(List<OutboundItem> items, List<List<Locking>> lockItemsByItem,
			List<Long> reservationIds) {
		List<OutboundItem> result = new ArrayList<>();
		int offset = 0;
		for (int i = 0; i < items.size(); i++) {
			List<Locking> locks = lockItemsByItem.get(i);
			List<Long> ids = reservationIds.subList(offset, offset + locks.size());
			List<OutboundBinAllocation> allocations = locks.stream()
					.collect(java.util.stream.Collectors.groupingBy(Locking::binId, java.util.LinkedHashMap::new,
							java.util.stream.Collectors.summingLong(Locking::quantity)))
					.entrySet().stream()
					.map(entry -> new OutboundBinAllocation(entry.getKey(), Math.toIntExact(entry.getValue())))
					.toList();
			OutboundItem item = items.get(i);
			result.add(new OutboundItem(item.skuId(), item.availableQty(), item.inspectionQty(), item.defectiveQty(),
					new ArrayList<>(ids), allocations, item.remark()));
			offset += locks.size();
		}
		return result;
	}

	private static OutboundPayload pendingOutboundPayload(TransferPayload payload, Long transferReceiptId) {
		List<OutboundItem> items = payload.items().stream()
				.map(item -> new OutboundItem(item.skuId(), item.availableQty(), item.inspectionQty(), item.defectiveQty(),
						List.of(), List.of(), item.remark()))
				.toList();
		return new OutboundPayload(TransactionType.OutTransfer, payload.sourceWarehouseId(), items, transferReceiptId,
				payload.remark(), payload.operatorId(), payload.submitTime());
	}

	private static InboundPayload pendingInboundPayload(Long transferReceiptId, TransferPayload payload) {
		List<InboundItem> items = payload.items().stream()
				.map(item -> new InboundItem(item.skuId(), item.availableQty(), item.availableTargetBinId(),
						item.inspectionQty(), item.inspectionTargetBinId(), item.defectiveQty(),
						item.defectiveTargetBinId(), item.remark()))
				.toList();
		return new InboundPayload(TransactionType.InTransfer, payload.targetWarehouseId(), null, null, items,
				payload.remark(), transferReceiptId, payload.operatorId(), payload.submitTime());
	}

	private Transition transition(ReceiptType type, String fromState, ReceiptEvent event) {
		for (Transition transition : transitions(type)) {
			if (transition.fromState().equals(fromState) && transition.event() == event) {
				return transition;
			}
		}
		throw new InventoryException("illegal transition from " + fromState + " by " + event);
	}

	private List<Transition> transitions(ReceiptType type) {
		return switch (type) {
			case INBOUND -> List.of(
					new Transition("Draft", ReceiptEvent.Cancel, "Cancelled"),
					new Transition("Draft", ReceiptEvent.Reserve, "PendingSubmission"),
					new Transition("PendingSubmission", ReceiptEvent.Reserve, "PendingSubmission"),
					new Transition("Draft", ReceiptEvent.Submit, "PendingInbound"),
					new Transition("PendingSubmission", ReceiptEvent.Submit, "PendingInbound"),
					new Transition("PendingInbound", ReceiptEvent.ConfirmInbound, "Completed"),
					new Transition("PendingInbound", ReceiptEvent.Rollback, "PendingSubmission"),
					new Transition("PendingSubmission", ReceiptEvent.Cancel, "Cancelled")
			);
			case OUTBOUND -> List.of(
					new Transition("Draft", ReceiptEvent.Cancel, "Cancelled"),
					new Transition("Draft", ReceiptEvent.Reserve, "PendingSubmission"),
					new Transition("PendingSubmission", ReceiptEvent.Reserve, "PendingSubmission"),
					new Transition("Draft", ReceiptEvent.Submit, "PendingOutbound"),
					new Transition("PendingSubmission", ReceiptEvent.Submit, "PendingOutbound"),
					new Transition("PendingOutbound", ReceiptEvent.ConfirmOutbound, "Completed"),
					new Transition("PendingOutbound", ReceiptEvent.Rollback, "PendingSubmission"),
					new Transition("PendingSubmission", ReceiptEvent.Cancel, "Cancelled")
			);
			case TRANSFER -> List.of(
					new Transition("Draft", ReceiptEvent.Cancel, "Cancelled"),
					new Transition("Draft", ReceiptEvent.Reserve, "PendingSubmission"),
					new Transition("Draft", ReceiptEvent.Submit, "PendingTransfer"),
					new Transition("PendingSubmission", ReceiptEvent.Reserve, "PendingSubmission"),
					new Transition("PendingSubmission", ReceiptEvent.Submit, "PendingTransfer"),
					new Transition("PendingTransfer", ReceiptEvent.Rollback, "PendingSubmission"),
					new Transition("PendingSubmission", ReceiptEvent.Cancel, "Cancelled"),
					new Transition("PendingTransfer", ReceiptEvent.ConfirmTransfer, "PendingTransferInbound"),
					new Transition("PendingTransferInbound", ReceiptEvent.ConfirmTransferInbound, "Completed")
			);
			case LOCATION_TRANSFER -> List.of(
					new Transition("Draft", ReceiptEvent.Cancel, "Cancelled"),
					new Transition("Draft", ReceiptEvent.Submit, "PendingLocationTransfer"),
					new Transition("Draft", ReceiptEvent.ConfirmLocationTransfer, "Completed"),
					new Transition("PendingLocationTransfer", ReceiptEvent.ConfirmLocationTransfer, "Completed"),
					new Transition("PendingLocationTransfer", ReceiptEvent.Cancel, "Cancelled")
			);
		};
	}

	private InventoryReceipt toReceipt(ReceiptRow row) {
		return new InventoryReceipt(row.id(), row.number(), ReceiptType.valueOf(row.typeKey()), row.state(), row.payload(),
				row.creator(), row.createTime(), row.operator(), row.recordTime(),
				"Completed".equals(row.state()) ? row.recordTime() : null);
	}

	private <T> T readPayload(JsonNode payload, Class<T> type) {
		try {
			return objectMapper.treeToValue(payload, type);
		}
		catch (Exception ex) {
			throw new InventoryException("invalid payload: " + ex.getMessage());
		}
	}

	private JsonNode valueToTree(Object value) {
		return objectMapper.valueToTree(value);
	}

	private void validatePayload(ReceiptType type, JsonNode payload) {
		switch (type) {
			case INBOUND -> {
				InboundPayload value = readPayload(payload, InboundPayload.class);
				if (value.warehouseId() == null || value.items() == null || value.items().isEmpty()) {
					throw new InventoryException("invalid inbound payload");
				}
			}
			case OUTBOUND -> readPayload(payload, OutboundPayload.class);
			case TRANSFER -> {
				TransferPayload value = readPayload(payload, TransferPayload.class);
				if (value.items() == null || value.items().isEmpty() || value.sourceWarehouseId() == null
						|| value.targetWarehouseId() == null || value.sourceWarehouseId().equals(value.targetWarehouseId())) {
					throw new InventoryException("invalid transfer payload");
				}
			}
			case LOCATION_TRANSFER -> readPayload(payload, LocationTransferPayload.class);
		}
	}

	private static JsonNode requirePayload(JsonNode payload) {
		if (payload == null || payload.isNull()) {
			throw new InventoryException("payload is required");
		}
		return payload;
	}

	private static String initialState(ReceiptType type) {
		return "Draft";
	}

	private static NumberType numberType(ReceiptType type) {
		return switch (type) {
			case INBOUND -> NumberType.INBOUND;
			case OUTBOUND -> NumberType.OUTBOUND;
			case TRANSFER -> NumberType.TRANSFER;
			case LOCATION_TRANSFER -> NumberType.LOCATION_TRANSFER;
		};
	}

	private static long availableQty(BinBalance balance) {
		return balance == null ? 0 : balance.quantity() - balance.lockQty();
	}

	private static Long binIdByName(List<Bin> bins, String name) {
		return bins.stream().filter(bin -> name.equals(bin.name())).map(Bin::id).findFirst().orElse(null);
	}

	private static Long resolveBin(int quantity, Long explicitBinId, Long defaultBinId, String label) {
		if (quantity <= 0) {
			return explicitBinId;
		}
		Long resolved = explicitBinId != null ? explicitBinId : defaultBinId;
		if (resolved == null) {
			throw new InventoryException("missing " + label + " inbound bin");
		}
		return resolved;
	}

	private static List<Long> nullableList(Long value) {
		return value == null ? List.of() : List.of(value);
	}

	private static String outLockRemark(TransactionType type) {
		return switch (type) {
			case OutSalesDelivery -> "发货出库锁定";
			case OutTransfer -> "调拨出库锁定";
			case OutReturn -> "退货出库锁定";
			case OutStockLoss -> "盘亏出库锁定";
			default -> "发货单配货锁定";
		};
	}

	private record LockPlan(LockRequest lockRequest, List<List<Locking>> lockItemsByItem) {}

	private record Transition(String fromState, ReceiptEvent event, String toState) {}
}

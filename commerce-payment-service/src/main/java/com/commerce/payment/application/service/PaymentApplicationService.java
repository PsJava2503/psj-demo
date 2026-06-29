package com.commerce.payment.application.service;

import com.commerce.messaging.PaymentPaidEvent;
import com.commerce.payment.PaymentAllocationRequest;
import com.commerce.payment.PreCreatePaymentRequest;
import com.commerce.payment.RefundAllocationRequest;
import com.commerce.payment.RefundPaymentRequest;
import com.commerce.payment.domain.model.PaymentAllocationQueryOptions;
import com.commerce.payment.application.port.PaymentUseCase;
import com.commerce.payment.domain.model.PaymentOrderQueryOptions;
import com.commerce.payment.domain.model.PaymentOrderStatus;
import com.commerce.payment.domain.model.PaymentOutboxEventStatus;
import com.commerce.payment.domain.model.PaymentReconcileRecordQueryOptions;
import com.commerce.payment.domain.model.PaymentRefundAllocationQueryOptions;
import com.commerce.payment.domain.model.PaymentRefundQueryOptions;
import com.commerce.payment.domain.service.PaymentDomainService;
import com.commerce.payment.application.service.reconcile.PaymentReconcileService;
import com.commerce.payment.infrastructure.alipay.AlipaySandboxClient;
import com.commerce.payment.infrastructure.alipay.AlipaySandboxClient.AlipayGatewayResponse;
import com.commerce.payment.infrastructure.persistence.IdempotencyRecordRepository;
import com.commerce.payment.infrastructure.persistence.PaymentAllocationRepository;
import com.commerce.payment.infrastructure.persistence.PaymentNotifyLogRepository;
import com.commerce.payment.infrastructure.persistence.PaymentOrderEntity;
import com.commerce.payment.infrastructure.persistence.PaymentOutboxEventRepository;
import com.commerce.payment.infrastructure.persistence.PaymentRepository;
import com.commerce.payment.infrastructure.persistence.PaymentReconcileRecordRepository;
import com.commerce.payment.infrastructure.persistence.PaymentRefundAllocationRepository;
import com.commerce.payment.infrastructure.persistence.PaymentRefundRepository;
import com.commerce.payment.infrastructure.persistence.model.IdempotencyRecordData;
import com.commerce.payment.infrastructure.persistence.model.PaymentAllocationData;
import com.commerce.payment.infrastructure.persistence.model.PaymentNotifyLogData;
import com.commerce.payment.infrastructure.persistence.model.PaymentOrderData;
import com.commerce.payment.infrastructure.persistence.model.PaymentOutboxEventData;
import com.commerce.payment.infrastructure.persistence.model.PaymentReconcileRecordData;
import com.commerce.payment.infrastructure.persistence.model.PaymentRefundAllocationData;
import com.commerce.payment.infrastructure.persistence.model.PaymentRefundData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

@Service
public class PaymentApplicationService implements PaymentUseCase {

	private final PaymentDomainService paymentDomainService;
	private final PaymentRepository paymentRepository;
	private final PaymentAllocationRepository paymentAllocationRepository;
	private final PaymentNotifyLogRepository paymentNotifyLogRepository;
	private final PaymentRefundRepository paymentRefundRepository;
	private final PaymentRefundAllocationRepository paymentRefundAllocationRepository;
	private final PaymentReconcileRecordRepository paymentReconcileRecordRepository;
	private final PaymentReconcileService paymentReconcileService;
	private final IdempotencyRecordRepository idempotencyRecordRepository;
	private final PaymentOutboxEventRepository paymentOutboxEventRepository;
	private final PaymentOutboxRelay paymentOutboxRelay;
	private final AlipaySandboxClient alipaySandboxClient;
	private final ObjectMapper objectMapper;
	private final int orderTimeoutMinutes;

	public PaymentApplicationService(
			PaymentDomainService paymentDomainService,
			PaymentRepository paymentRepository,
			PaymentAllocationRepository paymentAllocationRepository,
			PaymentNotifyLogRepository paymentNotifyLogRepository,
			PaymentRefundRepository paymentRefundRepository,
			PaymentRefundAllocationRepository paymentRefundAllocationRepository,
			PaymentReconcileRecordRepository paymentReconcileRecordRepository,
			PaymentReconcileService paymentReconcileService,
			IdempotencyRecordRepository idempotencyRecordRepository,
			PaymentOutboxEventRepository paymentOutboxEventRepository,
			PaymentOutboxRelay paymentOutboxRelay,
			AlipaySandboxClient alipaySandboxClient,
			ObjectMapper objectMapper,
			org.springframework.core.env.Environment environment
	) {
		this.paymentDomainService = paymentDomainService;
		this.paymentRepository = paymentRepository;
		this.paymentAllocationRepository = paymentAllocationRepository;
		this.paymentNotifyLogRepository = paymentNotifyLogRepository;
		this.paymentRefundRepository = paymentRefundRepository;
		this.paymentRefundAllocationRepository = paymentRefundAllocationRepository;
		this.paymentReconcileRecordRepository = paymentReconcileRecordRepository;
		this.paymentReconcileService = paymentReconcileService;
		this.idempotencyRecordRepository = idempotencyRecordRepository;
		this.paymentOutboxEventRepository = paymentOutboxEventRepository;
		this.paymentOutboxRelay = paymentOutboxRelay;
		this.alipaySandboxClient = alipaySandboxClient;
		this.objectMapper = objectMapper;
		this.orderTimeoutMinutes = environment.getProperty("payment.order-timeout-minutes", Integer.class, 30);
	}

	@Override
	@Transactional
	public String pay(Long orderId, BigDecimal amount) {
		return preCreate(orderId, amount, paymentDomainService.defaultSubject(orderId));
	}

	@Override
	@Transactional
	public String preCreate(Long orderId, BigDecimal amount, String subject) {
		return preCreate(new PreCreatePaymentRequest(
				orderId,
				amount,
				subject,
				List.of(new PaymentAllocationRequest(
						orderId,
						null,
						amount,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						amount,
						amount
				))
		));
	}

	@Override
	@Transactional
	public String preCreate(PreCreatePaymentRequest request) {
		Long orderId = request.checkoutOrderId();
		BigDecimal amount = request.totalAmount();
		paymentDomainService.validateRequest(orderId, amount);
		Optional<PaymentOrderEntity> existingOrder = paymentOrder(orderId);
		if (existingOrder.isPresent() && existingOrder.get().status() == PaymentOrderStatus.TRADE_SUCCESS) {
			return "PAID";
		}

		if (existingOrder.isPresent() && existingOrder.get().status() == PaymentOrderStatus.WAIT_BUYER_PAY) {
			return "WAIT_BUYER_PAY:" + existingOrder.get().outTradeNo();
		}
		if (existingOrder.isPresent() && existingOrder.get().status() == PaymentOrderStatus.TRADE_FAILED) {
			return retryPreCreate(existingOrder.get(), amount, request.subject());
		}
		if (existingOrder.isPresent()) {
			return existingOrder.get().status().name() + ":" + existingOrder.get().outTradeNo();
		}

		String outTradeNo = paymentDomainService.generateOutTradeNo(orderId);
		String finalSubject = StringUtils.hasText(request.subject()) ? request.subject() : paymentDomainService.defaultSubject(orderId);
		paymentRepository.create(new PaymentOrderData(
				null,
				orderId,
				outTradeNo,
				null,
				amount,
				finalSubject,
				PaymentOrderStatus.WAIT_BUYER_PAY.name(),
				"{}",
				null,
				null
		));
		PaymentOrderEntity paymentOrder = paymentOrder(orderId)
				.orElseThrow(() -> new IllegalStateException("payment order not found after create"));
		createAllocations(paymentOrder, normalizedAllocations(request, orderId, amount));

		AlipayGatewayResponse response = alipaySandboxClient.precreate(outTradeNo, amount, finalSubject);
		if (!response.success()) {
			updatePaymentOrder(outTradeNo, null, PaymentOrderStatus.TRADE_FAILED, response.rawBody());
			return "PAY_FAILED:" + response.code() + ":" + response.message();
		}

		String tradeNo = stringValue(response.payload().get("trade_no"));
		updatePaymentOrder(outTradeNo, tradeNo, PaymentOrderStatus.WAIT_BUYER_PAY, response.rawBody());
		String qrCode = stringValue(response.payload().get("qr_code"));
		return "WAIT_BUYER_PAY:" + outTradeNo + ":" + qrCode;
	}

	private String retryPreCreate(PaymentOrderEntity existingOrder, BigDecimal amount, String subject) {
		String finalSubject = StringUtils.hasText(subject) ? subject : existingOrder.subject();
		AlipayGatewayResponse response = alipaySandboxClient.precreate(existingOrder.outTradeNo(), amount, finalSubject);
		if (!response.success()) {
			updatePaymentOrder(existingOrder.outTradeNo(), null, PaymentOrderStatus.TRADE_FAILED, response.rawBody());
			return "PAY_FAILED:" + response.code() + ":" + response.message();
		}

		String tradeNo = stringValue(response.payload().get("trade_no"));
		updatePaymentOrder(existingOrder.outTradeNo(), tradeNo, PaymentOrderStatus.WAIT_BUYER_PAY, response.rawBody());
		String qrCode = stringValue(response.payload().get("qr_code"));
		return "WAIT_BUYER_PAY:" + existingOrder.outTradeNo() + ":" + qrCode;
	}

	@Override
	@Transactional
	public String handleNotify(Map<String, String> notifyParams) {
		String notifyId = notifyParams.getOrDefault("notify_id", "");
		String outTradeNo = notifyParams.getOrDefault("out_trade_no", "");
		String tradeStatus = notifyParams.getOrDefault("trade_status", "");
		String payload = notifyParams.toString();
		if (!StringUtils.hasText(outTradeNo)) {
			createNotifyLog(notifyId, outTradeNo, tradeStatus, false, payload);
			return "failure";
		}

		boolean verified = alipaySandboxClient.verifyNotifySignature(notifyParams);
		createNotifyLog(notifyId, outTradeNo, tradeStatus, verified, payload);
		if (!verified) {
			return "failure";
		}

		String dedupeKey = "notify:" + notifyId;
		if (StringUtils.hasText(notifyId) && !tryCreateIdempotencyRecord(dedupeKey)) {
			return "success";
		}

		PaymentOrderStatus newStatus = paymentDomainService.mapTradeStatus(tradeStatus);
		updatePaymentOrder(
				outTradeNo,
				notifyParams.getOrDefault("trade_no", null),
				newStatus,
				payload
		);
		createPaymentPaidOutboxIfNeeded(outTradeNo, notifyParams.getOrDefault("trade_no", null), newStatus);
		return "success";
	}

	@Override
	@Transactional
	public String refund(Long orderId, BigDecimal refundAmount, String reason) {
		return refund(new RefundPaymentRequest(
				orderId,
				"legacy-" + orderId + "-" + refundAmount.stripTrailingZeros().toPlainString(),
				refundAmount,
				reason,
				List.of(new RefundAllocationRequest(
						orderId,
						refundAmount,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						refundAmount
				))
		));
	}

	@Override
	@Transactional
	public String refund(RefundPaymentRequest request) {
		Long orderId = request.checkoutOrderId();
		BigDecimal refundAmount = request.refundAmount();
		paymentDomainService.validateRequest(orderId, refundAmount);
		PaymentOrderEntity order = paymentOrder(orderId)
				.orElseThrow(() -> new IllegalArgumentException("payment order not found"));
		if (order.status() != PaymentOrderStatus.TRADE_SUCCESS) {
			return "REFUND_FAILED:order_not_paid";
		}

		String outRefundNo = paymentDomainService.generateOutRefundNo(request.refundRequestId());
		String idempotencyKey = "refund:" + outRefundNo;
		if (!tryCreateIdempotencyRecord(idempotencyKey)) {
			return paymentRefundRepository.query(new PaymentRefundQueryOptions(
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.of(outRefundNo),
					Optional.empty()
			)).stream()
					.findFirst()
					.map(PaymentRefundData::status)
					.orElse("REFUND_DUPLICATED");
		}
		List<RefundAllocationRequest> allocations = normalizedRefundAllocations(request, order);
		validateRefundAllocations(order, allocations);

		AlipayGatewayResponse response = alipaySandboxClient.refund(
				order.outTradeNo(),
				refundAmount,
				outRefundNo,
				StringUtils.hasText(request.reason()) ? request.reason() : "normal_refund"
		);
		String status = response.success() ? "REFUND_SUCCESS" : "REFUND_FAILED";
		PaymentRefundData refundData = new PaymentRefundData(
				null,
				order.id(),
				order.checkoutOrderId(),
				order.outTradeNo(),
				outRefundNo,
				refundAmount,
				status,
				response.rawBody(),
				null
		);
		paymentRefundRepository.create(refundData);
		if (response.success()) {
			PaymentRefundData savedRefund = paymentRefundRepository.query(new PaymentRefundQueryOptions(
					Optional.empty(),
					Optional.of(order.id()),
					Optional.of(order.checkoutOrderId()),
					Optional.of(order.outTradeNo()),
					Optional.of(outRefundNo),
					Optional.empty()
			)).stream().findFirst().orElse(refundData);
			createRefundAllocations(savedRefund.id(), order.checkoutOrderId(), allocations);
		}
		return status;
	}

	@Override
	@Transactional
	public String close(Long orderId) {
		PaymentOrderEntity order = paymentOrder(orderId)
				.orElseThrow(() -> new IllegalArgumentException("payment order not found"));
		if (order.status() != PaymentOrderStatus.WAIT_BUYER_PAY) {
			return "CLOSE_SKIPPED:" + order.status().name();
		}
		AlipayGatewayResponse response = alipaySandboxClient.close(order.outTradeNo());
		if (!response.success()) {
			return "CLOSE_FAILED:" + response.code();
		}
		updatePaymentOrder(order.outTradeNo(), null, PaymentOrderStatus.TRADE_CLOSED, response.rawBody());
		return "TRADE_CLOSED";
	}

	@Override
	@Transactional
	public String query(Long orderId) {
		PaymentOrderEntity order = paymentOrder(orderId)
				.orElseThrow(() -> new IllegalArgumentException("payment order not found"));
		AlipayGatewayResponse response = alipaySandboxClient.query(order.outTradeNo());
		if (!response.success()) {
			updatePaymentOrder(order.outTradeNo(), order.tradeNo(), order.status(), response.rawBody());
			return "QUERY_FAILED:" + response.code() + ":" + response.message();
		}
		String tradeStatus = stringValue(response.payload().get("trade_status"));
		PaymentOrderStatus status = paymentDomainService.mapTradeStatus(tradeStatus);
		updatePaymentOrder(
				order.outTradeNo(),
				stringValue(response.payload().get("trade_no")),
				status,
				response.rawBody()
		);
		createPaymentPaidOutboxIfNeeded(order.outTradeNo(), stringValue(response.payload().get("trade_no")), status);
		return status.name();
	}

	@Override
	@Transactional
	public String reconcile(LocalDate billDate) {
		AlipayGatewayResponse response = alipaySandboxClient.downloadBill(billDate, "trade");
		String status = response.success() ? "RECONCILE_READY" : "RECONCILE_FAILED";
		String downloadUrl = stringValue(response.payload().get("bill_download_url"));
		upsertReconcileRecord(billDate, "trade", downloadUrl, status, response.rawBody());
		int detailCount = response.success() ? paymentReconcileService.reconcile(billDate, "trade", downloadUrl) : 0;
		return status + (StringUtils.hasText(downloadUrl) ? ":" + downloadUrl + ":details=" + detailCount : "");
	}

	@Override
	@Transactional
	public void closeTimedOutOrders() {
		PaymentOrderQueryOptions options = new PaymentOrderQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(PaymentOrderStatus.WAIT_BUYER_PAY),
				Optional.of(ZonedDateTime.now().minusMinutes(orderTimeoutMinutes))
		);
		for (PaymentOrderEntity order : paymentRepository.query(options)) {
			AlipayGatewayResponse response = alipaySandboxClient.close(order.outTradeNo());
			if (response.success()) {
				updatePaymentOrder(order.outTradeNo(), null, PaymentOrderStatus.TRADE_CLOSED, response.rawBody());
			}
		}
	}

	private List<PaymentAllocationRequest> normalizedAllocations(PreCreatePaymentRequest request, Long orderId, BigDecimal amount) {
		if (!request.allocations().isEmpty()) {
			BigDecimal total = request.allocations().stream()
					.map(PaymentAllocationRequest::paidAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			if (total.compareTo(amount) != 0) {
				throw new IllegalArgumentException("payment allocation total does not match payment amount");
			}
			return request.allocations();
		}
		return List.of(new PaymentAllocationRequest(
				orderId,
				null,
				amount,
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				BigDecimal.ZERO,
				amount,
				amount
		));
	}

	private void createAllocations(PaymentOrderEntity paymentOrder, List<PaymentAllocationRequest> allocations) {
		for (PaymentAllocationRequest allocation : allocations) {
			if (allocation.paidAmount() == null || allocation.paidAmount().signum() <= 0) {
				throw new IllegalArgumentException("invalid payment allocation amount");
			}
			paymentAllocationRepository.create(new PaymentAllocationData(
					null,
					paymentOrder.id(),
					paymentOrder.checkoutOrderId(),
					allocation.subOrderId(),
					allocation.merchantId(),
					defaultAmount(allocation.goodsAmount()),
					defaultAmount(allocation.shippingAmount()),
					defaultAmount(allocation.platformDiscountAmount()),
					defaultAmount(allocation.merchantDiscountAmount()),
					allocation.paidAmount(),
					allocation.settleAmount() == null ? allocation.paidAmount() : allocation.settleAmount(),
					null,
					null
			));
		}
	}

	private List<RefundAllocationRequest> normalizedRefundAllocations(RefundPaymentRequest request, PaymentOrderEntity order) {
		if (!request.allocations().isEmpty()) {
			BigDecimal total = request.allocations().stream()
					.map(RefundAllocationRequest::refundPaidAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			if (total.compareTo(request.refundAmount()) != 0) {
				throw new IllegalArgumentException("refund allocation total does not match refund amount");
			}
			return request.allocations();
		}
		return paymentAllocationRepository.query(new PaymentAllocationQueryOptions(
				Optional.empty(),
				Optional.of(order.id()),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		)).stream()
				.map(allocation -> new RefundAllocationRequest(
						allocation.subOrderId(),
						allocation.paidAmount(),
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						allocation.paidAmount()
				))
				.toList();
	}

	private void validateRefundAllocations(PaymentOrderEntity order, List<RefundAllocationRequest> allocations) {
		for (RefundAllocationRequest allocation : allocations) {
			PaymentAllocationData paymentAllocation = paymentAllocationRepository.query(new PaymentAllocationQueryOptions(
					Optional.empty(),
					Optional.of(order.id()),
					Optional.empty(),
					Optional.ofNullable(allocation.subOrderId()),
					Optional.empty()
			)).stream().findFirst()
					.orElseThrow(() -> new IllegalArgumentException("payment allocation not found for sub order " + allocation.subOrderId()));
			BigDecimal refunded = paymentRefundAllocationRepository.query(new PaymentRefundAllocationQueryOptions(
					Optional.empty(),
					Optional.empty(),
					Optional.ofNullable(paymentAllocation.id()),
					Optional.empty(),
					Optional.empty(),
					Optional.empty()
			)).stream()
					.map(PaymentRefundAllocationData::refundPaidAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal requestedTotal = refunded.add(allocation.refundPaidAmount());
			if (requestedTotal.compareTo(paymentAllocation.paidAmount()) > 0) {
				throw new IllegalArgumentException("refund amount exceeds allocation paid amount");
			}
		}
	}

	private void createRefundAllocations(Long paymentRefundId, Long checkoutOrderId, List<RefundAllocationRequest> allocations) {
		for (RefundAllocationRequest allocation : allocations) {
			PaymentAllocationData paymentAllocation = paymentAllocationRepository.query(new PaymentAllocationQueryOptions(
					Optional.empty(),
					Optional.empty(),
					Optional.of(checkoutOrderId),
					Optional.ofNullable(allocation.subOrderId()),
					Optional.empty()
			)).stream().findFirst().orElse(null);
			paymentRefundAllocationRepository.create(new PaymentRefundAllocationData(
					null,
					paymentRefundId,
					paymentAllocation == null ? null : paymentAllocation.id(),
					checkoutOrderId,
					allocation.subOrderId(),
					paymentAllocation == null ? null : paymentAllocation.merchantId(),
					defaultAmount(allocation.refundGoodsAmount()),
					defaultAmount(allocation.refundShippingAmount()),
					defaultAmount(allocation.refundPlatformDiscountAmount()),
					defaultAmount(allocation.refundMerchantDiscountAmount()),
					allocation.refundPaidAmount(),
					null
			));
		}
	}

	private BigDecimal defaultAmount(BigDecimal amount) {
		return amount == null ? BigDecimal.ZERO : amount;
	}

	private String stringValue(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	private Optional<PaymentOrderEntity> paymentOrder(Long orderId) {
		return paymentRepository.query(new PaymentOrderQueryOptions(
				Optional.empty(),
				Optional.ofNullable(orderId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		)).stream().findFirst();
	}

	private void updatePaymentOrder(String outTradeNo, String tradeNo, PaymentOrderStatus status, String rawResponse) {
		paymentRepository.update(
				new PaymentOrderData(
						null,
						null,
						null,
						tradeNo,
						null,
						null,
						status.name(),
						rawResponse,
						null,
						null
				),
				new PaymentOrderQueryOptions(
						Optional.empty(),
						Optional.empty(),
						Optional.ofNullable(outTradeNo),
						Optional.empty(),
						Optional.empty()
				)
		);
	}

	private void createPaymentPaidOutboxIfNeeded(String outTradeNo, String tradeNo, PaymentOrderStatus status) {
		if (status != PaymentOrderStatus.TRADE_SUCCESS) {
			return;
		}
		Optional<PaymentOrderEntity> order = paymentRepository.query(new PaymentOrderQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.ofNullable(outTradeNo),
				Optional.empty(),
				Optional.empty()
		)).stream().findFirst();
		if (order.isEmpty()) {
			return;
		}
		String eventKey = "payment-paid:" + outTradeNo;
		PaymentPaidEvent event = new PaymentPaidEvent(order.get().checkoutOrderId(), outTradeNo, tradeNo, order.get().amount());
		try {
			paymentOutboxEventRepository.create(new PaymentOutboxEventData(
					null,
					eventKey,
					"PAYMENT_PAID",
					"payment_order",
					outTradeNo,
					objectMapper.writeValueAsString(event),
					PaymentOutboxEventStatus.NEW.name(),
					0,
					null,
					ZonedDateTime.now(),
					null,
					null
			));
			publishOutboxAfterCommit(eventKey);
		} catch (DuplicateKeyException ignored) {
			// The event was already recorded; relay will publish or retry it.
		} catch (JsonProcessingException ex) {
			throw new IllegalStateException("failed to serialize payment paid event", ex);
		}
	}

	private void publishOutboxAfterCommit(String eventKey) {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			paymentOutboxRelay.publishByEventKey(eventKey);
			return;
		}
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				paymentOutboxRelay.publishByEventKey(eventKey);
			}
		});
	}

	private void createNotifyLog(String notifyId, String outTradeNo, String tradeStatus, boolean verified, String payload) {
		paymentNotifyLogRepository.create(new PaymentNotifyLogData(
				null,
				notifyId,
				outTradeNo,
				tradeStatus,
				verified,
				payload,
				null
		));
	}

	private boolean tryCreateIdempotencyRecord(String idempotencyKey) {
		try {
			idempotencyRecordRepository.create(new IdempotencyRecordData(null, idempotencyKey, null));
			return true;
		} catch (DuplicateKeyException ignored) {
			return false;
		}
	}

	private void upsertReconcileRecord(
			LocalDate billDate,
			String billType,
			String downloadUrl,
			String status,
			String rawResponse
	) {
		PaymentReconcileRecordQueryOptions options = new PaymentReconcileRecordQueryOptions(
				Optional.ofNullable(billDate),
				Optional.ofNullable(billType)
		);
		PaymentReconcileRecordData data = new PaymentReconcileRecordData(
				null,
				billDate,
				billType,
				downloadUrl,
				status,
				rawResponse,
				null,
				null
		);
		if (paymentReconcileRecordRepository.query(options).isEmpty()) {
			paymentReconcileRecordRepository.create(data);
			return;
		}
		paymentReconcileRecordRepository.update(data, options);
	}

}

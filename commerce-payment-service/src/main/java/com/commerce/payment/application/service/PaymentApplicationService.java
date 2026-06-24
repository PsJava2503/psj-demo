package com.commerce.payment.application.service;

import com.commerce.messaging.PaymentPaidEvent;
import com.commerce.payment.application.port.PaymentUseCase;
import com.commerce.payment.domain.model.PaymentOrderQueryOptions;
import com.commerce.payment.domain.model.PaymentOrderStatus;
import com.commerce.payment.domain.model.PaymentOutboxEventStatus;
import com.commerce.payment.domain.model.PaymentReconcileRecordQueryOptions;
import com.commerce.payment.domain.service.PaymentDomainService;
import com.commerce.payment.infrastructure.alipay.AlipaySandboxClient;
import com.commerce.payment.infrastructure.alipay.AlipaySandboxClient.AlipayGatewayResponse;
import com.commerce.payment.infrastructure.persistence.IdempotencyRecordRepository;
import com.commerce.payment.infrastructure.persistence.PaymentNotifyLogRepository;
import com.commerce.payment.infrastructure.persistence.PaymentOrderEntity;
import com.commerce.payment.infrastructure.persistence.PaymentOutboxEventRepository;
import com.commerce.payment.infrastructure.persistence.PaymentRepository;
import com.commerce.payment.infrastructure.persistence.PaymentReconcileRecordRepository;
import com.commerce.payment.infrastructure.persistence.PaymentRefundRepository;
import com.commerce.payment.infrastructure.persistence.model.IdempotencyRecordData;
import com.commerce.payment.infrastructure.persistence.model.PaymentNotifyLogData;
import com.commerce.payment.infrastructure.persistence.model.PaymentOrderData;
import com.commerce.payment.infrastructure.persistence.model.PaymentOutboxEventData;
import com.commerce.payment.infrastructure.persistence.model.PaymentReconcileRecordData;
import com.commerce.payment.infrastructure.persistence.model.PaymentRefundData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
	private final PaymentNotifyLogRepository paymentNotifyLogRepository;
	private final PaymentRefundRepository paymentRefundRepository;
	private final PaymentReconcileRecordRepository paymentReconcileRecordRepository;
	private final IdempotencyRecordRepository idempotencyRecordRepository;
	private final PaymentOutboxEventRepository paymentOutboxEventRepository;
	private final PaymentOutboxRelay paymentOutboxRelay;
	private final AlipaySandboxClient alipaySandboxClient;
	private final ObjectMapper objectMapper;
	private final int orderTimeoutMinutes;

	public PaymentApplicationService(
			PaymentDomainService paymentDomainService,
			PaymentRepository paymentRepository,
			PaymentNotifyLogRepository paymentNotifyLogRepository,
			PaymentRefundRepository paymentRefundRepository,
			PaymentReconcileRecordRepository paymentReconcileRecordRepository,
			IdempotencyRecordRepository idempotencyRecordRepository,
			PaymentOutboxEventRepository paymentOutboxEventRepository,
			PaymentOutboxRelay paymentOutboxRelay,
			AlipaySandboxClient alipaySandboxClient,
			ObjectMapper objectMapper,
			org.springframework.core.env.Environment environment
	) {
		this.paymentDomainService = paymentDomainService;
		this.paymentRepository = paymentRepository;
		this.paymentNotifyLogRepository = paymentNotifyLogRepository;
		this.paymentRefundRepository = paymentRefundRepository;
		this.paymentReconcileRecordRepository = paymentReconcileRecordRepository;
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
		paymentDomainService.validateRequest(orderId, amount);
		Optional<PaymentOrderEntity> existingOrder = paymentOrder(orderId);
		if (existingOrder.isPresent() && existingOrder.get().status() == PaymentOrderStatus.TRADE_SUCCESS) {
			return "PAID";
		}

		if (existingOrder.isPresent() && existingOrder.get().status() == PaymentOrderStatus.WAIT_BUYER_PAY) {
			return "WAIT_BUYER_PAY:" + existingOrder.get().outTradeNo();
		}
		if (existingOrder.isPresent() && existingOrder.get().status() == PaymentOrderStatus.TRADE_FAILED) {
			return retryPreCreate(existingOrder.get(), amount, subject);
		}
		if (existingOrder.isPresent()) {
			return existingOrder.get().status().name() + ":" + existingOrder.get().outTradeNo();
		}

		String outTradeNo = paymentDomainService.generateOutTradeNo(orderId);
		String finalSubject = StringUtils.hasText(subject) ? subject : paymentDomainService.defaultSubject(orderId);
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
		paymentDomainService.validateRequest(orderId, refundAmount);
		PaymentOrderEntity order = paymentOrder(orderId)
				.orElseThrow(() -> new IllegalArgumentException("payment order not found"));
		if (order.status() != PaymentOrderStatus.TRADE_SUCCESS) {
			return "REFUND_FAILED:order_not_paid";
		}

		String outRefundNo = paymentDomainService.generateOutRefundNo(orderId);
		String idempotencyKey = "refund:" + outRefundNo;
		if (!tryCreateIdempotencyRecord(idempotencyKey)) {
			return "REFUND_DUPLICATED";
		}

		AlipayGatewayResponse response = alipaySandboxClient.refund(
				order.outTradeNo(),
				refundAmount,
				outRefundNo,
				StringUtils.hasText(reason) ? reason : "normal_refund"
		);
		String status = response.success() ? "REFUND_SUCCESS" : "REFUND_FAILED";
		paymentRefundRepository.create(new PaymentRefundData(
				null,
				order.id(),
				order.checkoutOrderId(),
				order.outTradeNo(),
				outRefundNo,
				refundAmount,
				status,
				response.rawBody(),
				null
		));
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
		return status + (StringUtils.hasText(downloadUrl) ? ":" + downloadUrl : "");
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

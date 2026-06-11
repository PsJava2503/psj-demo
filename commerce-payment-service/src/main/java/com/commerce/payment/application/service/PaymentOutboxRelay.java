package com.commerce.payment.application.service;

import com.commerce.messaging.PaymentPaidEvent;
import com.commerce.payment.domain.model.PaymentOutboxEventQueryOptions;
import com.commerce.payment.domain.model.PaymentOutboxEventStatus;
import com.commerce.payment.infrastructure.mq.PaymentEventPublisher;
import com.commerce.payment.infrastructure.persistence.PaymentOutboxEventRepository;
import com.commerce.payment.infrastructure.persistence.model.PaymentOutboxEventData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentOutboxRelay {

	private static final Logger log = LoggerFactory.getLogger(PaymentOutboxRelay.class);
	private static final String PAYMENT_PAID = "PAYMENT_PAID";

	private final PaymentOutboxEventRepository paymentOutboxEventRepository;
	private final PaymentEventPublisher paymentEventPublisher;
	private final ObjectMapper objectMapper;

	public PaymentOutboxRelay(
			PaymentOutboxEventRepository paymentOutboxEventRepository,
			PaymentEventPublisher paymentEventPublisher,
			ObjectMapper objectMapper
	) {
		this.paymentOutboxEventRepository = paymentOutboxEventRepository;
		this.paymentEventPublisher = paymentEventPublisher;
		this.objectMapper = objectMapper;
	}

	@Scheduled(fixedDelayString = "${payment.outbox-relay-delay-ms:5000}")
	public void publishPendingEvents() {
		publishPendingEvents(PaymentOutboxEventStatus.NEW);
		publishPendingEvents(PaymentOutboxEventStatus.FAILED);
		publishPendingEvents(PaymentOutboxEventStatus.PUBLISHING);
	}

	public void publishByEventKey(String eventKey) {
		PaymentOutboxEventQueryOptions options = new PaymentOutboxEventQueryOptions(
				Optional.empty(),
				Optional.ofNullable(eventKey),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
		for (PaymentOutboxEventData event : paymentOutboxEventRepository.query(options)) {
			publishOne(event);
		}
	}

	private void publishPendingEvents(PaymentOutboxEventStatus status) {
		PaymentOutboxEventQueryOptions options = new PaymentOutboxEventQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(status),
				Optional.of(ZonedDateTime.now())
		);
		for (PaymentOutboxEventData event : paymentOutboxEventRepository.query(options)) {
			publishOne(event);
		}
	}

	private void publishOne(PaymentOutboxEventData event) {
		if (PaymentOutboxEventStatus.PUBLISHED.name().equals(event.status())) {
			return;
		}
		if (!claim(event)) {
			return;
		}
		try {
			if (PAYMENT_PAID.equals(event.eventType())) {
				paymentEventPublisher.publishPaymentPaid(objectMapper.readValue(event.payload(), PaymentPaidEvent.class));
				markPublished(event);
				return;
			}
			throw new IllegalArgumentException("unsupported payment outbox event type: " + event.eventType());
		} catch (Exception ex) {
			markFailed(event, ex);
		}
	}

	private boolean claim(PaymentOutboxEventData event) {
		int updated = paymentOutboxEventRepository.update(new PaymentOutboxEventData(
				null,
				null,
				null,
				null,
				null,
				null,
				PaymentOutboxEventStatus.PUBLISHING.name(),
				event.attemptCount(),
				event.lastError(),
				ZonedDateTime.now().plusSeconds(60),
				null,
				null
		), new PaymentOutboxEventQueryOptions(
				Optional.ofNullable(event.id()),
				Optional.empty(),
				Optional.empty(),
				Optional.of(PaymentOutboxEventStatus.valueOf(event.status())),
				Optional.empty()
		));
		return updated == 1;
	}

	private void markPublished(PaymentOutboxEventData event) {
		paymentOutboxEventRepository.update(new PaymentOutboxEventData(
				null,
				null,
				null,
				null,
				null,
				null,
				PaymentOutboxEventStatus.PUBLISHED.name(),
				nextAttemptCount(event),
				"",
				ZonedDateTime.now(),
				null,
				null
		), byId(event.id()));
	}

	private void markFailed(PaymentOutboxEventData event, Exception ex) {
		int attemptCount = nextAttemptCount(event);
		ZonedDateTime nextRetryTime = ZonedDateTime.now().plusSeconds(retryDelaySeconds(attemptCount));
		paymentOutboxEventRepository.update(new PaymentOutboxEventData(
				null,
				null,
				null,
				null,
				null,
				null,
				PaymentOutboxEventStatus.FAILED.name(),
				attemptCount,
				ex.getMessage(),
				nextRetryTime,
				null,
				null
		), byId(event.id()));
		log.warn("Failed to publish payment outbox event: eventKey={}, attempt={}", event.eventKey(), attemptCount, ex);
	}

	private PaymentOutboxEventQueryOptions byId(Long id) {
		return new PaymentOutboxEventQueryOptions(
				Optional.ofNullable(id),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	private int nextAttemptCount(PaymentOutboxEventData event) {
		return (event.attemptCount() == null ? 0 : event.attemptCount()) + 1;
	}

	private long retryDelaySeconds(int attemptCount) {
		return Math.min(300L, Math.max(5L, attemptCount * 10L));
	}
}

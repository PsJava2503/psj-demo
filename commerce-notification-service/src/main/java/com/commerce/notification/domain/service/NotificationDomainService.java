package com.commerce.notification.domain.service;

import com.commerce.notification.domain.model.NotificationRecordQueryOptions;
import com.commerce.notification.infrastructure.persistence.NotificationRecordRepository;
import com.commerce.notification.infrastructure.persistence.model.NotificationRecordData;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationDomainService {

	private static final Logger log = LoggerFactory.getLogger(NotificationDomainService.class);

	private final NotificationRecordRepository notificationRecordRepository;

	public NotificationDomainService(NotificationRecordRepository notificationRecordRepository) {
		this.notificationRecordRepository = notificationRecordRepository;
	}

	public void notifyOrderWaitPay(Long orderId) {
		notifyOrder(orderId, "ORDER_WAIT_PAY", "order-wait-pay:" + orderId);
	}

	public void notifyOrderPaid(Long orderId) {
		notifyOrder(orderId, "ORDER_PAID", "order-paid:" + orderId);
	}

	public void notifyOrderCancelled(Long orderId) {
		notifyOrder(orderId, "ORDER_CANCELLED", "order-cancelled:" + orderId);
	}

	private void notifyOrder(Long orderId, String templateCode, String idempotencyKey) {
		NotificationRecordQueryOptions options = new NotificationRecordQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(idempotencyKey)
		);
		if (!notificationRecordRepository.query(options).isEmpty()) {
			return;
		}
		notificationRecordRepository.create(new NotificationRecordData(
				null,
				orderId,
				null,
				templateCode,
				"LOG",
				null,
				"{\"orderId\":" + orderId + "}",
				"PENDING",
				idempotencyKey,
				null,
				null,
				null,
				null
		));
		try {
			log.info("Order notification sent, templateCode={}, orderId={}", templateCode, orderId);
			notificationRecordRepository.update(new NotificationRecordData(
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					"SENT",
					null,
					ZonedDateTime.now(),
					"",
					null,
					null
			), options);
		} catch (Exception ex) {
			notificationRecordRepository.update(new NotificationRecordData(
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					"FAILED",
					null,
					null,
					ex.getMessage(),
					null,
					null
			), options);
			throw ex;
		}
	}
}

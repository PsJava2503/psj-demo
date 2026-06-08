package com.psj.commerce.notification.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationDomainService {

	private static final Logger log = LoggerFactory.getLogger(NotificationDomainService.class);

	public void notifyOrderPaid(Long orderId) {
		log.info("Order paid notification sent, orderId={}", orderId);
	}

}

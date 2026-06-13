package com.commerce.notification.application.service;

import com.commerce.notification.application.port.NotificationUseCase;
import com.commerce.notification.domain.service.NotificationDomainService;
import org.springframework.stereotype.Service;

@Service
public class NotificationApplicationService implements NotificationUseCase {

	private final NotificationDomainService notificationDomainService;

	public NotificationApplicationService(NotificationDomainService notificationDomainService) {
		this.notificationDomainService = notificationDomainService;
	}

	@Override
	public void notifyOrderWaitPay(Long orderId) {
		notificationDomainService.notifyOrderWaitPay(orderId);
	}

	@Override
	public void notifyOrderPaid(Long orderId) {
		notificationDomainService.notifyOrderPaid(orderId);
	}

	@Override
	public void notifyOrderCancelled(Long orderId) {
		notificationDomainService.notifyOrderCancelled(orderId);
	}

}

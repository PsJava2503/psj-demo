package com.psj.commerce.notification.application.service;

import com.psj.commerce.notification.application.port.NotificationUseCase;
import com.psj.commerce.notification.domain.service.NotificationDomainService;
import org.springframework.stereotype.Service;

@Service
public class NotificationApplicationService implements NotificationUseCase {

	private final NotificationDomainService notificationDomainService;

	public NotificationApplicationService(NotificationDomainService notificationDomainService) {
		this.notificationDomainService = notificationDomainService;
	}

	@Override
	public void notifyOrderPaid(Long orderId) {
		notificationDomainService.notifyOrderPaid(orderId);
	}

}

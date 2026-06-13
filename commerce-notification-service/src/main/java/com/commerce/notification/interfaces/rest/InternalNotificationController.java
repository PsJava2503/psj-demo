package com.commerce.notification.interfaces.rest;

import com.commerce.notification.application.port.NotificationUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/notifications")
public class InternalNotificationController {

	private final NotificationUseCase notificationUseCase;

	public InternalNotificationController(NotificationUseCase notificationUseCase) {
		this.notificationUseCase = notificationUseCase;
	}

	@PostMapping("/order-paid")
	public void notifyOrderPaid(@RequestParam Long orderId) {
		notificationUseCase.notifyOrderPaid(orderId);
	}

	@PostMapping("/order-wait-pay")
	public void notifyOrderWaitPay(@RequestParam Long orderId) {
		notificationUseCase.notifyOrderWaitPay(orderId);
	}

	@PostMapping("/order-cancelled")
	public void notifyOrderCancelled(@RequestParam Long orderId) {
		notificationUseCase.notifyOrderCancelled(orderId);
	}
}

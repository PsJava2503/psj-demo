package com.psj.commerce.notification.interfaces.rest;

import com.psj.commerce.notification.application.port.NotificationUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationUseCase notificationUseCase;

	public NotificationController(NotificationUseCase notificationUseCase) {
		this.notificationUseCase = notificationUseCase;
	}

	@PostMapping("/mock")
	public String mockNotify() {
		notificationUseCase.notifyOrderPaid(1L);
		return "notification sent";
	}

	@PostMapping("/order-paid")
	public void notifyOrderPaid(@RequestParam Long orderId) {
		notificationUseCase.notifyOrderPaid(orderId);
	}

}

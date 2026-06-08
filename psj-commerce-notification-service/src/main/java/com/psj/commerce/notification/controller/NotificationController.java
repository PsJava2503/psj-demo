package com.psj.commerce.notification.controller;

import com.psj.commerce.api.NotificationRpcService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationRpcService notificationRpcService;

	public NotificationController(NotificationRpcService notificationRpcService) {
		this.notificationRpcService = notificationRpcService;
	}

	@PostMapping("/mock")
	public String mockNotify() {
		notificationRpcService.notifyOrderPaid(1L);
		return "notification sent";
	}

}

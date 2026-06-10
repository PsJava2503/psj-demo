package com.psj.commerce.order.infrastructure.rpc;

import com.psj.commerce.order.application.port.NotificationCommandPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "psj-commerce-notification-service")
public interface NotificationClient extends NotificationCommandPort {

	@Override
	@PostMapping("/api/notifications/order-paid")
	void notifyOrderPaid(@RequestParam("orderId") Long orderId);

}

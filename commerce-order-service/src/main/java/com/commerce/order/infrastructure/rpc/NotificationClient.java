package com.commerce.order.infrastructure.rpc;

import com.commerce.order.application.port.NotificationCommandPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commerce-notification-service")
public interface NotificationClient extends NotificationCommandPort {

	@Override
	@PostMapping("/internal/notifications/order-wait-pay")
	void notifyOrderWaitPay(@RequestParam("orderId") Long orderId);

	@Override
	@PostMapping("/internal/notifications/order-paid")
	void notifyOrderPaid(@RequestParam("orderId") Long orderId);

	@Override
	@PostMapping("/internal/notifications/order-cancelled")
	void notifyOrderCancelled(@RequestParam("orderId") Long orderId);

}

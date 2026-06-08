package com.psj.commerce.order.infrastructure.rpc;

import com.psj.commerce.api.NotificationRpcService;
import com.psj.commerce.order.application.port.NotificationCommandPort;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class NotificationClient implements NotificationCommandPort {

	@DubboReference(check = false)
	private NotificationRpcService notificationRpcService;

	@Override
	public void notifyOrderPaid(Long orderId) {
		notificationRpcService.notifyOrderPaid(orderId);
	}

}

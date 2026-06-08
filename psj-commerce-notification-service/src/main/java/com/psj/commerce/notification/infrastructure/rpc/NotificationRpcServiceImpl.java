package com.psj.commerce.notification.infrastructure.rpc;

import com.psj.commerce.api.NotificationRpcService;
import com.psj.commerce.notification.application.port.NotificationUseCase;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class NotificationRpcServiceImpl implements NotificationRpcService {

	private final NotificationUseCase notificationUseCase;

	public NotificationRpcServiceImpl(NotificationUseCase notificationUseCase) {
		this.notificationUseCase = notificationUseCase;
	}

	@Override
	public void notifyOrderPaid(Long orderId) {
		notificationUseCase.notifyOrderPaid(orderId);
	}

}

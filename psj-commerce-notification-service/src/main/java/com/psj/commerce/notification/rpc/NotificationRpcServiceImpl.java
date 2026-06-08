package com.psj.commerce.notification.rpc;

import com.psj.commerce.api.NotificationRpcService;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class NotificationRpcServiceImpl implements NotificationRpcService {

	@Override
	public void notifyOrderPaid(Long orderId) {
		System.out.println("Order paid notification sent, orderId=" + orderId);
	}

}

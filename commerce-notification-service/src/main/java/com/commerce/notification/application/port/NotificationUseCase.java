package com.commerce.notification.application.port;

public interface NotificationUseCase {

	void notifyOrderWaitPay(Long orderId);

	void notifyOrderPaid(Long orderId);

	void notifyOrderCancelled(Long orderId);

}

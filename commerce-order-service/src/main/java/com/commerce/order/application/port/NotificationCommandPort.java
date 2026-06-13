package com.commerce.order.application.port;

public interface NotificationCommandPort {

	void notifyOrderWaitPay(Long orderId);

	void notifyOrderPaid(Long orderId);

	void notifyOrderCancelled(Long orderId);

}

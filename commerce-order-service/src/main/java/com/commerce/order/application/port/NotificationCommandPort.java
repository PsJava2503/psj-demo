package com.commerce.order.application.port;

public interface NotificationCommandPort {

	void notifyOrderPaid(Long orderId);

}

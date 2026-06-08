package com.psj.commerce.order.application.port;

public interface NotificationCommandPort {

	void notifyOrderPaid(Long orderId);

}

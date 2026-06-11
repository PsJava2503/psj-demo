package com.commerce.payment.application.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface PaymentUseCase {

	String pay(Long orderId, BigDecimal amount);

	String preCreate(Long orderId, BigDecimal amount, String subject);

	String handleNotify(Map<String, String> notifyParams);

	String refund(Long orderId, BigDecimal refundAmount, String reason);

	String close(Long orderId);

	String query(Long orderId);

	String reconcile(LocalDate billDate);

	void closeTimedOutOrders();

}

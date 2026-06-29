package com.commerce.payment.application.port;

import com.commerce.payment.PreCreatePaymentRequest;
import com.commerce.payment.RefundPaymentRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface PaymentUseCase {

	String pay(Long orderId, BigDecimal amount);

	String preCreate(Long orderId, BigDecimal amount, String subject);

	String preCreate(PreCreatePaymentRequest request);

	String handleNotify(Map<String, String> notifyParams);

	String refund(Long orderId, BigDecimal refundAmount, String reason);

	String refund(RefundPaymentRequest request);

	String close(Long orderId);

	String query(Long orderId);

	String reconcile(LocalDate billDate);

	void closeTimedOutOrders();

}

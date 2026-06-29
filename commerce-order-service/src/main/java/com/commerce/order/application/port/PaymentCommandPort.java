package com.commerce.order.application.port;

import com.commerce.payment.PreCreatePaymentRequest;
import com.commerce.payment.RefundPaymentRequest;
import com.commerce.payment.PaymentOrderSummaryResponse;
import java.math.BigDecimal;

public interface PaymentCommandPort {

	String preCreate(Long orderId, BigDecimal amount, String subject);

	String refund(Long orderId, BigDecimal amount, String reason);

	String preCreateDetailed(PreCreatePaymentRequest request);

	String refundDetailed(RefundPaymentRequest request);

	String close(Long orderId);

	PaymentOrderSummaryResponse summary(Long orderId);

}

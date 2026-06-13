package com.commerce.order.application.port;

import java.math.BigDecimal;

public interface PaymentCommandPort {

	String preCreate(Long orderId, BigDecimal amount, String subject);

	String refund(Long orderId, BigDecimal amount, String reason);

}

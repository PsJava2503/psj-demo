package com.commerce.payment.application.port;

import java.math.BigDecimal;

public interface PaymentUseCase {

	String pay(Long orderId, BigDecimal amount);

}

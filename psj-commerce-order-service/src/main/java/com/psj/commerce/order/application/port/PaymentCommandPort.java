package com.psj.commerce.order.application.port;

import java.math.BigDecimal;

public interface PaymentCommandPort {

	String pay(Long orderId, BigDecimal amount);

}

package com.psj.commerce.api;

import java.math.BigDecimal;

public interface PaymentRpcService {

	String pay(Long orderId, BigDecimal amount);

}

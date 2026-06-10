package com.commerce.payment.domain.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class PaymentDomainService {

	public String pay(Long orderId, BigDecimal amount) {
		if (orderId == null || amount == null || amount.signum() <= 0) {
			return "PAY_FAILED";
		}
		return "PAID";
	}

}

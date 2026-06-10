package com.commerce.payment.application.service;

import com.commerce.payment.application.port.PaymentUseCase;
import com.commerce.payment.domain.service.PaymentDomainService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class PaymentApplicationService implements PaymentUseCase {

	private final PaymentDomainService paymentDomainService;

	public PaymentApplicationService(PaymentDomainService paymentDomainService) {
		this.paymentDomainService = paymentDomainService;
	}

	@Override
	public String pay(Long orderId, BigDecimal amount) {
		return paymentDomainService.pay(orderId, amount);
	}

}

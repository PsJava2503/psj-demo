package com.commerce.payment.application.service;

import com.commerce.payment.application.port.PaymentUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentTimeoutScheduler {

	private final PaymentUseCase paymentUseCase;

	public PaymentTimeoutScheduler(PaymentUseCase paymentUseCase) {
		this.paymentUseCase = paymentUseCase;
	}

	@Scheduled(fixedDelayString = "${payment.close-scan-delay-ms:60000}")
	public void closeTimedOutOrders() {
		paymentUseCase.closeTimedOutOrders();
	}
}

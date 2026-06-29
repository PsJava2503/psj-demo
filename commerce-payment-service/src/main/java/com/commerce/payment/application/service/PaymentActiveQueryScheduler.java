package com.commerce.payment.application.service;

import com.commerce.payment.application.port.PaymentUseCase;
import com.commerce.payment.domain.model.PaymentOrderQueryOptions;
import com.commerce.payment.domain.model.PaymentOrderStatus;
import com.commerce.payment.infrastructure.persistence.PaymentRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentActiveQueryScheduler {

	private final PaymentRepository paymentRepository;
	private final PaymentUseCase paymentUseCase;

	public PaymentActiveQueryScheduler(PaymentRepository paymentRepository, PaymentUseCase paymentUseCase) {
		this.paymentRepository = paymentRepository;
		this.paymentUseCase = paymentUseCase;
	}

	@Scheduled(fixedDelayString = "${payment.active-query-delay-ms:60000}")
	public void queryWaitingPayments() {
		PaymentOrderQueryOptions options = new PaymentOrderQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(PaymentOrderStatus.WAIT_BUYER_PAY),
				Optional.of(ZonedDateTime.now().minusMinutes(1))
		);
		paymentRepository.query(options).forEach(order -> paymentUseCase.query(order.checkoutOrderId()));
	}
}

package com.psj.commerce.payment.interfaces.rest;

import com.psj.commerce.payment.application.port.PaymentUseCase;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentUseCase paymentUseCase;

	public PaymentController(PaymentUseCase paymentUseCase) {
		this.paymentUseCase = paymentUseCase;
	}

	@PostMapping("/mock")
	public String mockPay() {
		return paymentUseCase.pay(1L, BigDecimal.valueOf(99));
	}

	@PostMapping("/pay")
	public String pay(@RequestParam Long orderId, @RequestParam BigDecimal amount) {
		return paymentUseCase.pay(orderId, amount);
	}

}

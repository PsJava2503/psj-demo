package com.psj.commerce.payment.controller;

import com.psj.commerce.api.PaymentRpcService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentRpcService paymentRpcService;

	public PaymentController(PaymentRpcService paymentRpcService) {
		this.paymentRpcService = paymentRpcService;
	}

	@PostMapping("/mock")
	public String mockPay() {
		return paymentRpcService.pay(1L, BigDecimal.valueOf(99));
	}

}

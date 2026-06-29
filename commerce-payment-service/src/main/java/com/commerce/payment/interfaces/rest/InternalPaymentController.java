package com.commerce.payment.interfaces.rest;

import com.commerce.payment.application.port.PaymentUseCase;
import com.commerce.payment.PreCreatePaymentRequest;
import com.commerce.payment.RefundPaymentRequest;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/payments")
public class InternalPaymentController {

	private final PaymentUseCase paymentUseCase;

	public InternalPaymentController(PaymentUseCase paymentUseCase) {
		this.paymentUseCase = paymentUseCase;
	}

	@PostMapping("/pay")
	public String pay(@RequestParam Long orderId, @RequestParam BigDecimal amount) {
		return paymentUseCase.pay(orderId, amount);
	}

	@PostMapping("/precreate")
	public String preCreate(
			@RequestParam Long orderId,
			@RequestParam BigDecimal amount,
			@RequestParam(required = false) String subject
	) {
		return paymentUseCase.preCreate(orderId, amount, subject);
	}

	@PostMapping("/precreate-detailed")
	public String preCreateDetailed(@RequestBody PreCreatePaymentRequest request) {
		return paymentUseCase.preCreate(request);
	}

	@GetMapping("/query")
	public String query(@RequestParam Long orderId) {
		return paymentUseCase.query(orderId);
	}

	@PostMapping("/close")
	public String close(@RequestParam Long orderId) {
		return paymentUseCase.close(orderId);
	}

	@PostMapping("/refund")
	public String refund(
			@RequestParam Long orderId,
			@RequestParam BigDecimal amount,
			@RequestParam(required = false) String reason
	) {
		return paymentUseCase.refund(orderId, amount, reason);
	}

	@PostMapping("/refund-detailed")
	public String refundDetailed(@RequestBody RefundPaymentRequest request) {
		return paymentUseCase.refund(request);
	}
}

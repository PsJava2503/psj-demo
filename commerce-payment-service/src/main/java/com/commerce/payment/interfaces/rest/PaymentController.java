package com.commerce.payment.interfaces.rest;

import com.commerce.payment.application.port.PaymentUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

	@PostMapping("/precreate")
	public String preCreate(
			@RequestParam Long orderId,
			@RequestParam BigDecimal amount,
			@RequestParam(required = false) String subject
	) {
		return paymentUseCase.preCreate(orderId, amount, subject);
	}

	@PostMapping("/notify")
	public String notifyAlipay(@RequestParam Map<String, String> notifyParams) {
		return paymentUseCase.handleNotify(notifyParams);
	}

	@PostMapping("/refund")
	public String refund(
			@RequestParam Long orderId,
			@RequestParam BigDecimal amount,
			@RequestParam(required = false) String reason
	) {
		return paymentUseCase.refund(orderId, amount, reason);
	}

	@PostMapping("/close")
	public String close(@RequestParam Long orderId) {
		return paymentUseCase.close(orderId);
	}

	@GetMapping("/query")
	public String query(@RequestParam Long orderId) {
		return paymentUseCase.query(orderId);
	}

	@PostMapping("/reconcile")
	public String reconcile(@RequestParam String billDate) {
		return paymentUseCase.reconcile(LocalDate.parse(billDate));
	}

}

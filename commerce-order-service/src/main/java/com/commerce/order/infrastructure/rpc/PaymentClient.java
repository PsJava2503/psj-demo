package com.commerce.order.infrastructure.rpc;

import com.commerce.order.application.port.PaymentCommandPort;
import com.commerce.payment.PaymentOrderSummaryResponse;
import com.commerce.payment.PreCreatePaymentRequest;
import com.commerce.payment.RefundPaymentRequest;
import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commerce-payment-service")
public interface PaymentClient extends PaymentCommandPort {

	@Override
	@PostMapping("/internal/payments/precreate")
	String preCreate(
			@RequestParam("orderId") Long orderId,
			@RequestParam("amount") BigDecimal amount,
			@RequestParam("subject") String subject
	);

	@Override
	@PostMapping("/internal/payments/precreate-detailed")
	String preCreateDetailed(@RequestBody PreCreatePaymentRequest request);

	@Override
	@PostMapping("/internal/payments/refund")
	String refund(
			@RequestParam("orderId") Long orderId,
			@RequestParam("amount") BigDecimal amount,
			@RequestParam("reason") String reason
	);

	@Override
	@PostMapping("/internal/payments/refund-detailed")
	String refundDetailed(@RequestBody RefundPaymentRequest request);

	@Override
	@PostMapping("/internal/payments/close")
	String close(@RequestParam("orderId") Long orderId);

	@Override
	@GetMapping("/internal/payments/summary")
	PaymentOrderSummaryResponse summary(@RequestParam("orderId") Long orderId);

}

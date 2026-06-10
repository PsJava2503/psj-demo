package com.commerce.order.infrastructure.rpc;

import com.commerce.order.application.port.PaymentCommandPort;
import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "commerce-payment-service")
public interface PaymentClient extends PaymentCommandPort {

	@Override
	@PostMapping("/internal/payments/pay")
	String pay(@RequestParam("orderId") Long orderId, @RequestParam("amount") BigDecimal amount);

}

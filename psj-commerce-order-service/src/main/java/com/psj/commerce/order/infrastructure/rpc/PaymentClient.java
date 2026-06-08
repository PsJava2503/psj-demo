package com.psj.commerce.order.infrastructure.rpc;

import com.psj.commerce.api.PaymentRpcService;
import com.psj.commerce.order.application.port.PaymentCommandPort;
import java.math.BigDecimal;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class PaymentClient implements PaymentCommandPort {

	@DubboReference(check = false)
	private PaymentRpcService paymentRpcService;

	@Override
	public String pay(Long orderId, BigDecimal amount) {
		return paymentRpcService.pay(orderId, amount);
	}

}

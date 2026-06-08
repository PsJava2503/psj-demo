package com.psj.commerce.payment.infrastructure.rpc;

import com.psj.commerce.api.PaymentRpcService;
import com.psj.commerce.payment.application.port.PaymentUseCase;
import java.math.BigDecimal;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class PaymentRpcServiceImpl implements PaymentRpcService {

	private final PaymentUseCase paymentUseCase;

	public PaymentRpcServiceImpl(PaymentUseCase paymentUseCase) {
		this.paymentUseCase = paymentUseCase;
	}

	@Override
	public String pay(Long orderId, BigDecimal amount) {
		return paymentUseCase.pay(orderId, amount);
	}

}

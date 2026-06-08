package com.psj.commerce.payment.rpc;

import com.psj.commerce.api.PaymentRpcService;
import java.math.BigDecimal;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class PaymentRpcServiceImpl implements PaymentRpcService {

	@Override
	public String pay(Long orderId, BigDecimal amount) {
		return "PAID";
	}

}

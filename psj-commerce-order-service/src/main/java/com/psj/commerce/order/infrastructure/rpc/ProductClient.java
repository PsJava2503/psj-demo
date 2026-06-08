package com.psj.commerce.order.infrastructure.rpc;

import com.psj.commerce.api.ProductRpcService;
import com.psj.commerce.order.application.port.ProductQueryPort;
import java.math.BigDecimal;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class ProductClient implements ProductQueryPort {

	@DubboReference(check = false)
	private ProductRpcService productRpcService;

	@Override
	public BigDecimal getPrice(Long productId) {
		return productRpcService.getPrice(productId);
	}

}

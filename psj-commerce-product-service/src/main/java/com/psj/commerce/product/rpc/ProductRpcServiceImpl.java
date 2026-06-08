package com.psj.commerce.product.rpc;

import com.psj.commerce.api.ProductRpcService;
import java.math.BigDecimal;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class ProductRpcServiceImpl implements ProductRpcService {

	@Override
	public BigDecimal getPrice(Long productId) {
		return BigDecimal.valueOf(99);
	}

}

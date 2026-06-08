package com.psj.commerce.product.infrastructure.rpc;

import com.psj.commerce.api.ProductRpcService;
import com.psj.commerce.product.application.port.ProductUseCase;
import java.math.BigDecimal;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class ProductRpcServiceImpl implements ProductRpcService {

	private final ProductUseCase productUseCase;

	public ProductRpcServiceImpl(ProductUseCase productUseCase) {
		this.productUseCase = productUseCase;
	}

	@Override
	public BigDecimal getPrice(Long productId) {
		return productUseCase.getPrice(productId);
	}

}

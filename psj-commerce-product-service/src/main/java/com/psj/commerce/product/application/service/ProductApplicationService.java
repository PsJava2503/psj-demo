package com.psj.commerce.product.application.service;

import com.psj.commerce.product.application.port.ProductUseCase;
import com.psj.commerce.product.domain.service.ProductDomainService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class ProductApplicationService implements ProductUseCase {

	private final ProductDomainService productDomainService;

	public ProductApplicationService(ProductDomainService productDomainService) {
		this.productDomainService = productDomainService;
	}

	@Override
	public BigDecimal getPrice(Long productId) {
		return productDomainService.priceOf(productId);
	}

}

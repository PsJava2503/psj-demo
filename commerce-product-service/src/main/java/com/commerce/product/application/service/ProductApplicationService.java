package com.commerce.product.application.service;

import com.commerce.product.application.port.ProductUseCase;
import com.commerce.product.domain.service.ProductDomainService;
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

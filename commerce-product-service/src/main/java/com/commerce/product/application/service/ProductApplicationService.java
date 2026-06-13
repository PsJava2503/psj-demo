package com.commerce.product.application.service;

import com.commerce.product.application.port.ProductUseCase;
import com.commerce.product.domain.model.Product;
import com.commerce.product.domain.model.ProductQueryOptions;
import com.commerce.product.domain.service.ProductDomainService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductApplicationService implements ProductUseCase {

	private final ProductDomainService productDomainService;

	public ProductApplicationService(ProductDomainService productDomainService) {
		this.productDomainService = productDomainService;
	}

	@Override
	public int create(Product product) {
		return productDomainService.create(product);
	}

	@Override
	public int update(Product product, ProductQueryOptions options) {
		return productDomainService.update(product, options);
	}

	@Override
	public int delete(ProductQueryOptions options) {
		return productDomainService.delete(options);
	}

	@Override
	public List<Product> query(ProductQueryOptions options) {
		return productDomainService.query(options);
	}

	@Override
	public BigDecimal getPrice(Long productId) {
		return productDomainService.priceOf(productId);
	}

}

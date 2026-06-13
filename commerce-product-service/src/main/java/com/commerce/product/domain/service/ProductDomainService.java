package com.commerce.product.domain.service;

import com.commerce.product.domain.model.Product;
import com.commerce.product.domain.model.ProductQueryOptions;
import com.commerce.product.domain.port.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProductDomainService {

	private final ProductRepository productRepository;

	public ProductDomainService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public int create(Product product) {
		return productRepository.create(product);
	}

	public int update(Product product, ProductQueryOptions options) {
		return productRepository.update(product, options);
	}

	public int delete(ProductQueryOptions options) {
		return productRepository.delete(options);
	}

	public List<Product> query(ProductQueryOptions options) {
		return productRepository.query(options);
	}

	public BigDecimal priceOf(Long productId) {
		if (productId == null) {
			return BigDecimal.ZERO;
		}
		return productRepository.query(new ProductQueryOptions(
				Optional.of(productId),
				Optional.empty(),
				Optional.empty(),
				Optional.of(true),
				Optional.of(false)
		)).stream()
				.findFirst()
				.map(Product::price)
				.orElse(BigDecimal.ZERO);
	}

}

package com.commerce.product.interfaces.rest;

import com.commerce.product.ProductResponse;
import com.commerce.product.application.port.ProductUseCase;
import com.commerce.product.domain.model.Product;
import com.commerce.product.domain.model.ProductQueryOptions;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/products")
public class InternalProductController {

	private final ProductUseCase productUseCase;

	public InternalProductController(ProductUseCase productUseCase) {
		this.productUseCase = productUseCase;
	}

	@GetMapping("/{productId}/price")
	public BigDecimal getPrice(@PathVariable Long productId) {
		return productUseCase.getPrice(productId);
	}

	@GetMapping("/{productId}")
	public ProductResponse getProduct(@PathVariable Long productId) {
		return productUseCase.query(new ProductQueryOptions(
				Optional.of(productId),
				Optional.empty(),
				Optional.empty(),
				Optional.of(true),
				Optional.of(false)
		)).stream()
				.findFirst()
				.map(this::toResponse)
				.orElseThrow(() -> new IllegalArgumentException("product not found: " + productId));
	}

	private ProductResponse toResponse(Product product) {
		return new ProductResponse(
				product.id(),
				product.name(),
				product.price(),
				product.skuId(),
				product.enabled()
		);
	}
}

package com.psj.commerce.product.interfaces.rest;

import com.psj.commerce.product.application.port.ProductUseCase;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductUseCase productUseCase;

	public ProductController(ProductUseCase productUseCase) {
		this.productUseCase = productUseCase;
	}

	@GetMapping("/{productId}/price")
	public BigDecimal price(@PathVariable Long productId) {
		return productUseCase.getPrice(productId);
	}

}

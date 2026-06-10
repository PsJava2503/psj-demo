package com.psj.commerce.product.interfaces.rest;

import com.psj.commerce.product.application.port.ProductUseCase;
import java.math.BigDecimal;
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
}

package com.psj.commerce.product.controller;

import com.psj.commerce.api.ProductRpcService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductRpcService productRpcService;

	public ProductController(ProductRpcService productRpcService) {
		this.productRpcService = productRpcService;
	}

	@GetMapping("/{productId}/price")
	public BigDecimal price(@PathVariable Long productId) {
		return productRpcService.getPrice(productId);
	}

}

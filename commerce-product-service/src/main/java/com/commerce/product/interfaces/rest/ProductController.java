package com.commerce.product.interfaces.rest;

import com.commerce.product.application.port.ProductUseCase;
import com.commerce.product.domain.model.Product;
import com.commerce.product.domain.model.ProductQueryOptions;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductUseCase productUseCase;

	public ProductController(ProductUseCase productUseCase) {
		this.productUseCase = productUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public int create(@RequestBody Product product) {
		return productUseCase.create(product);
	}

	@PutMapping("/{productId}")
	public int update(@PathVariable Long productId, @RequestBody Product product) {
		return productUseCase.update(new Product(
				productId,
				product.name(),
				product.price(),
				product.skuId(),
				product.enabled(),
				product.deleted(),
				product.createTime(),
				product.updateTime()
		), new ProductQueryOptions(
				Optional.of(productId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		));
	}

	@DeleteMapping("/{productId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long productId) {
		productUseCase.delete(new ProductQueryOptions(
				Optional.of(productId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		));
	}

	@GetMapping
	public List<Product> query(
			@RequestParam Optional<Long> id,
			@RequestParam Optional<String> name,
			@RequestParam Optional<Long> skuId,
			@RequestParam Optional<Boolean> enabled,
			@RequestParam Optional<Boolean> deleted
	) {
		return productUseCase.query(new ProductQueryOptions(id, name, skuId, enabled, deleted));
	}

	@GetMapping("/{productId}")
	public Optional<Product> get(@PathVariable Long productId) {
		return productUseCase.query(new ProductQueryOptions(
				Optional.of(productId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(false)
		)).stream().findFirst();
	}

	@GetMapping("/{productId}/price")
	public BigDecimal price(@PathVariable Long productId) {
		return productUseCase.getPrice(productId);
	}

}

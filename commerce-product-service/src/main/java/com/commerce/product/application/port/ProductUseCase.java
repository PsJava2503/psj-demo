package com.commerce.product.application.port;

import com.commerce.product.domain.model.Product;
import com.commerce.product.domain.model.ProductQueryOptions;
import java.math.BigDecimal;
import java.util.List;

public interface ProductUseCase {

	int create(Product product);

	int update(Product product, ProductQueryOptions options);

	int delete(ProductQueryOptions options);

	List<Product> query(ProductQueryOptions options);

	BigDecimal getPrice(Long productId);

}

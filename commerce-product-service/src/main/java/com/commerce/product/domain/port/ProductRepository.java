package com.commerce.product.domain.port;

import com.commerce.product.domain.model.Product;
import com.commerce.product.domain.model.ProductQueryOptions;
import java.util.List;

public interface ProductRepository {

	int create(Product product);

	int update(Product product, ProductQueryOptions options);

	int delete(ProductQueryOptions options);

	List<Product> query(ProductQueryOptions options);
}

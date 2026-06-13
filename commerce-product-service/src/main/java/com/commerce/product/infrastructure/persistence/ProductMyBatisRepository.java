package com.commerce.product.infrastructure.persistence;

import com.commerce.product.domain.model.Product;
import com.commerce.product.domain.model.ProductQueryOptions;
import com.commerce.product.domain.port.ProductRepository;
import com.commerce.product.infrastructure.persistence.mapper.ProductDynamicMapper;
import com.commerce.product.infrastructure.persistence.model.ProductData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ProductMyBatisRepository implements ProductRepository {

	private final ProductDynamicMapper productDynamicMapper;

	public ProductMyBatisRepository(ProductDynamicMapper productDynamicMapper) {
		this.productDynamicMapper = productDynamicMapper;
	}

	@Override
	public int create(Product product) {
		return productDynamicMapper.create(toData(product));
	}

	@Override
	public int update(Product product, ProductQueryOptions options) {
		return productDynamicMapper.update(toData(product), options);
	}

	@Override
	public int delete(ProductQueryOptions options) {
		return productDynamicMapper.delete(options);
	}

	@Override
	public List<Product> query(ProductQueryOptions options) {
		return productDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private Product toDomain(ProductData data) {
		return new Product(
				data.id(),
				data.name(),
				data.price(),
				data.skuId(),
				data.enabled(),
				data.deleted(),
				data.createTime(),
				data.updateTime()
		);
	}

	private ProductData toData(Product product) {
		return new ProductData(
				product.id(),
				product.name(),
				product.price(),
				product.skuId(),
				product.enabled(),
				product.deleted(),
				product.createTime(),
				product.updateTime()
		);
	}
}

package com.commerce.order.application.port;

import com.commerce.product.ProductResponse;

public interface ProductQueryPort {

	ProductResponse getProduct(Long productId);

}

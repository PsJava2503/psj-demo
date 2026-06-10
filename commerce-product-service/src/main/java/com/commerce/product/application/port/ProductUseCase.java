package com.commerce.product.application.port;

import java.math.BigDecimal;

public interface ProductUseCase {

	BigDecimal getPrice(Long productId);

}

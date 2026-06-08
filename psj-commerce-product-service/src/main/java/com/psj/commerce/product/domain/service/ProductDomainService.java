package com.psj.commerce.product.domain.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class ProductDomainService {

	public BigDecimal priceOf(Long productId) {
		if (productId == null) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(99);
	}

}

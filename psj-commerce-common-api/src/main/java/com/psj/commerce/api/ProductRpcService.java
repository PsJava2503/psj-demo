package com.psj.commerce.api;

import java.math.BigDecimal;

public interface ProductRpcService {

	BigDecimal getPrice(Long productId);

}

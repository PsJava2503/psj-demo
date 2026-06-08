package com.psj.commerce.order.application.port;

import java.math.BigDecimal;

public interface ProductQueryPort {

	BigDecimal getPrice(Long productId);

}

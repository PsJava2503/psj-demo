package com.commerce.payment;

import java.math.BigDecimal;

public record PaymentAllocationRequest(
		Long subOrderId,
		Long merchantId,
		BigDecimal goodsAmount,
		BigDecimal shippingAmount,
		BigDecimal platformDiscountAmount,
		BigDecimal merchantDiscountAmount,
		BigDecimal paidAmount,
		BigDecimal settleAmount
) {
}

package com.commerce.payment;

import java.math.BigDecimal;

public record RefundAllocationRequest(
		Long subOrderId,
		BigDecimal refundGoodsAmount,
		BigDecimal refundShippingAmount,
		BigDecimal refundPlatformDiscountAmount,
		BigDecimal refundMerchantDiscountAmount,
		BigDecimal refundPaidAmount
) {
}

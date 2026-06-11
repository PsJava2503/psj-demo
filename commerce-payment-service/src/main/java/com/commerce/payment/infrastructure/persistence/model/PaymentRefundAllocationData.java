package com.commerce.payment.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PaymentRefundAllocationData(
		Long id,
		Long paymentRefundId,
		Long paymentAllocationId,
		Long checkoutOrderId,
		Long subOrderId,
		Long merchantId,
		BigDecimal refundGoodsAmount,
		BigDecimal refundShippingAmount,
		BigDecimal refundPlatformDiscountAmount,
		BigDecimal refundMerchantDiscountAmount,
		BigDecimal refundPaidAmount,
		ZonedDateTime createTime
) {}

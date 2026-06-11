package com.commerce.payment.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PaymentAllocationData(
		Long id,
		Long paymentOrderId,
		Long checkoutOrderId,
		Long subOrderId,
		Long merchantId,
		BigDecimal goodsAmount,
		BigDecimal shippingAmount,
		BigDecimal platformDiscountAmount,
		BigDecimal merchantDiscountAmount,
		BigDecimal paidAmount,
		BigDecimal settleAmount,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {}

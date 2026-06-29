package com.commerce.payment;

import java.math.BigDecimal;
import java.util.List;

public record PaymentOrderSummaryResponse(
		Long checkoutOrderId,
		String outTradeNo,
		String tradeNo,
		BigDecimal amount,
		String status,
		List<PaymentAllocationResponse> allocations
) {
	public List<PaymentAllocationResponse> allocations() {
		return allocations == null ? List.of() : allocations;
	}
}

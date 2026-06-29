package com.commerce.payment;

import java.math.BigDecimal;
import java.util.List;

public record PreCreatePaymentRequest(
		Long checkoutOrderId,
		BigDecimal totalAmount,
		String subject,
		List<PaymentAllocationRequest> allocations
) {
	public List<PaymentAllocationRequest> allocations() {
		return allocations == null ? List.of() : allocations;
	}
}

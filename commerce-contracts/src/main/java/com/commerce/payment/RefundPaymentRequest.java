package com.commerce.payment;

import java.math.BigDecimal;
import java.util.List;

public record RefundPaymentRequest(
		Long checkoutOrderId,
		String refundRequestId,
		BigDecimal refundAmount,
		String reason,
		List<RefundAllocationRequest> allocations
) {
	public List<RefundAllocationRequest> allocations() {
		return allocations == null ? List.of() : allocations;
	}
}

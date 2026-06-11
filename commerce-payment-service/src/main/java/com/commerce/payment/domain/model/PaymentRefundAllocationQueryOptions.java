package com.commerce.payment.domain.model;

import java.util.Optional;

public class PaymentRefundAllocationQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> paymentRefundId;
	private final Optional<Long> paymentAllocationId;
	private final Optional<Long> checkoutOrderId;
	private final Optional<Long> subOrderId;
	private final Optional<Long> merchantId;

	public PaymentRefundAllocationQueryOptions(
			Optional<Long> id,
			Optional<Long> paymentRefundId,
			Optional<Long> paymentAllocationId,
			Optional<Long> checkoutOrderId,
			Optional<Long> subOrderId,
			Optional<Long> merchantId
	) {
		this.id = id == null ? Optional.empty() : id;
		this.paymentRefundId = paymentRefundId == null ? Optional.empty() : paymentRefundId;
		this.paymentAllocationId = paymentAllocationId == null ? Optional.empty() : paymentAllocationId;
		this.checkoutOrderId = checkoutOrderId == null ? Optional.empty() : checkoutOrderId;
		this.subOrderId = subOrderId == null ? Optional.empty() : subOrderId;
		this.merchantId = merchantId == null ? Optional.empty() : merchantId;
	}

	public static PaymentRefundAllocationQueryOptions none() {
		return new PaymentRefundAllocationQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public Long getPaymentRefundIdValue() {
		return paymentRefundId.orElse(null);
	}

	public Long getPaymentAllocationIdValue() {
		return paymentAllocationId.orElse(null);
	}

	public Long getCheckoutOrderIdValue() {
		return checkoutOrderId.orElse(null);
	}

	public Long getSubOrderIdValue() {
		return subOrderId.orElse(null);
	}

	public Long getMerchantIdValue() {
		return merchantId.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent()
				|| paymentRefundId.isPresent()
				|| paymentAllocationId.isPresent()
				|| checkoutOrderId.isPresent()
				|| subOrderId.isPresent()
				|| merchantId.isPresent();
	}
}

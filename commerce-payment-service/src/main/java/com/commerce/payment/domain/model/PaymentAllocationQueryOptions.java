package com.commerce.payment.domain.model;

import java.util.Optional;

public class PaymentAllocationQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> paymentOrderId;
	private final Optional<Long> checkoutOrderId;
	private final Optional<Long> subOrderId;
	private final Optional<Long> merchantId;

	public PaymentAllocationQueryOptions(
			Optional<Long> id,
			Optional<Long> paymentOrderId,
			Optional<Long> checkoutOrderId,
			Optional<Long> subOrderId,
			Optional<Long> merchantId
	) {
		this.id = id == null ? Optional.empty() : id;
		this.paymentOrderId = paymentOrderId == null ? Optional.empty() : paymentOrderId;
		this.checkoutOrderId = checkoutOrderId == null ? Optional.empty() : checkoutOrderId;
		this.subOrderId = subOrderId == null ? Optional.empty() : subOrderId;
		this.merchantId = merchantId == null ? Optional.empty() : merchantId;
	}

	public static PaymentAllocationQueryOptions none() {
		return new PaymentAllocationQueryOptions(
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

	public Long getPaymentOrderIdValue() {
		return paymentOrderId.orElse(null);
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
				|| paymentOrderId.isPresent()
				|| checkoutOrderId.isPresent()
				|| subOrderId.isPresent()
				|| merchantId.isPresent();
	}
}

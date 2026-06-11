package com.commerce.payment.domain.model;

import java.util.Optional;

public class PaymentRefundQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> paymentOrderId;
	private final Optional<Long> checkoutOrderId;
	private final Optional<String> outTradeNo;
	private final Optional<String> outRefundNo;
	private final Optional<String> status;

	public PaymentRefundQueryOptions(
			Optional<Long> id,
			Optional<Long> paymentOrderId,
			Optional<Long> checkoutOrderId,
			Optional<String> outTradeNo,
			Optional<String> outRefundNo,
			Optional<String> status
	) {
		this.id = id == null ? Optional.empty() : id;
		this.paymentOrderId = paymentOrderId == null ? Optional.empty() : paymentOrderId;
		this.checkoutOrderId = checkoutOrderId == null ? Optional.empty() : checkoutOrderId;
		this.outTradeNo = outTradeNo == null ? Optional.empty() : outTradeNo;
		this.outRefundNo = outRefundNo == null ? Optional.empty() : outRefundNo;
		this.status = status == null ? Optional.empty() : status;
	}

	public static PaymentRefundQueryOptions none() {
		return new PaymentRefundQueryOptions(
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

	public Long getPaymentOrderIdValue() {
		return paymentOrderId.orElse(null);
	}

	public Long getCheckoutOrderIdValue() {
		return checkoutOrderId.orElse(null);
	}

	public String getOutTradeNoValue() {
		return outTradeNo.orElse(null);
	}

	public String getOutRefundNoValue() {
		return outRefundNo.orElse(null);
	}

	public String getStatusValue() {
		return status.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent()
				|| paymentOrderId.isPresent()
				|| checkoutOrderId.isPresent()
				|| outTradeNo.isPresent()
				|| outRefundNo.isPresent()
				|| status.isPresent();
	}
}

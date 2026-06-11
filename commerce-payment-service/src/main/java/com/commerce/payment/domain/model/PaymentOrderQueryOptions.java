package com.commerce.payment.domain.model;

import java.time.ZonedDateTime;
import java.util.Optional;

public class PaymentOrderQueryOptions {

	private final Optional<Long> id;
	private final Optional<Long> checkoutOrderId;
	private final Optional<String> outTradeNo;
	private final Optional<PaymentOrderStatus> status;
	private final Optional<ZonedDateTime> createTimeBefore;

	public PaymentOrderQueryOptions(
			Optional<Long> id,
			Optional<Long> checkoutOrderId,
			Optional<String> outTradeNo,
			Optional<PaymentOrderStatus> status,
			Optional<ZonedDateTime> createTimeBefore
	) {
		this.id = id == null ? Optional.empty() : id;
		this.checkoutOrderId = checkoutOrderId == null ? Optional.empty() : checkoutOrderId;
		this.outTradeNo = outTradeNo == null ? Optional.empty() : outTradeNo;
		this.status = status == null ? Optional.empty() : status;
		this.createTimeBefore = createTimeBefore == null ? Optional.empty() : createTimeBefore;
	}

	public static PaymentOrderQueryOptions none() {
		return new PaymentOrderQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	public Optional<Long> getId() {
		return id;
	}

	public Optional<Long> getCheckoutOrderId() {
		return checkoutOrderId;
	}

	public Optional<String> getOutTradeNo() {
		return outTradeNo;
	}

	public Optional<PaymentOrderStatus> getStatus() {
		return status;
	}

	public Optional<ZonedDateTime> getCreateTimeBefore() {
		return createTimeBefore;
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public Long getCheckoutOrderIdValue() {
		return checkoutOrderId.orElse(null);
	}

	public String getOutTradeNoValue() {
		return outTradeNo.orElse(null);
	}

	public String getStatusValue() {
		return status.map(PaymentOrderStatus::name).orElse(null);
	}

	public ZonedDateTime getCreateTimeBeforeValue() {
		return createTimeBefore.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent()
				|| checkoutOrderId.isPresent()
				|| outTradeNo.isPresent()
				|| status.isPresent()
				|| createTimeBefore.isPresent();
	}
}

package com.commerce.payment.domain.model;

import java.util.Optional;

public class PaymentNotifyLogQueryOptions {

	private final Optional<Long> id;
	private final Optional<String> notifyId;
	private final Optional<String> outTradeNo;
	private final Optional<String> tradeStatus;
	private final Optional<Boolean> verified;

	public PaymentNotifyLogQueryOptions(
			Optional<Long> id,
			Optional<String> notifyId,
			Optional<String> outTradeNo,
			Optional<String> tradeStatus,
			Optional<Boolean> verified
	) {
		this.id = id == null ? Optional.empty() : id;
		this.notifyId = notifyId == null ? Optional.empty() : notifyId;
		this.outTradeNo = outTradeNo == null ? Optional.empty() : outTradeNo;
		this.tradeStatus = tradeStatus == null ? Optional.empty() : tradeStatus;
		this.verified = verified == null ? Optional.empty() : verified;
	}

	public static PaymentNotifyLogQueryOptions none() {
		return new PaymentNotifyLogQueryOptions(
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

	public String getNotifyIdValue() {
		return notifyId.orElse(null);
	}

	public String getOutTradeNoValue() {
		return outTradeNo.orElse(null);
	}

	public String getTradeStatusValue() {
		return tradeStatus.orElse(null);
	}

	public Boolean getVerifiedValue() {
		return verified.orElse(null);
	}

	public boolean hasConditions() {
		return id.isPresent()
				|| notifyId.isPresent()
				|| outTradeNo.isPresent()
				|| tradeStatus.isPresent()
				|| verified.isPresent();
	}
}

package com.commerce.payment.domain.model;

import java.time.LocalDate;
import java.util.Optional;

public class PaymentReconcileRecordQueryOptions {

	private final Optional<LocalDate> billDate;
	private final Optional<String> billType;

	public PaymentReconcileRecordQueryOptions(Optional<LocalDate> billDate, Optional<String> billType) {
		this.billDate = billDate == null ? Optional.empty() : billDate;
		this.billType = billType == null ? Optional.empty() : billType;
	}

	public static PaymentReconcileRecordQueryOptions none() {
		return new PaymentReconcileRecordQueryOptions(Optional.empty(), Optional.empty());
	}

	public Optional<LocalDate> getBillDate() {
		return billDate;
	}

	public Optional<String> getBillType() {
		return billType;
	}

	public LocalDate getBillDateValue() {
		return billDate.orElse(null);
	}

	public String getBillTypeValue() {
		return billType.orElse(null);
	}

	public boolean hasConditions() {
		return billDate.isPresent() || billType.isPresent();
	}
}

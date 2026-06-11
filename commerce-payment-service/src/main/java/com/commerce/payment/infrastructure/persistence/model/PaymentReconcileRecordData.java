package com.commerce.payment.infrastructure.persistence.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public record PaymentReconcileRecordData(
		Long id,
		LocalDate billDate,
		String billType,
		String downloadUrl,
		String status,
		String rawResponse,
		ZonedDateTime createTime,
		ZonedDateTime updateTime
) {}

package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.PaymentRefundAllocationQueryOptions;
import com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicMapper;
import com.commerce.payment.infrastructure.persistence.model.PaymentRefundAllocationData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRefundAllocationRepository {

	private final PaymentRefundAllocationDynamicMapper paymentRefundAllocationDynamicMapper;

	public PaymentRefundAllocationRepository(PaymentRefundAllocationDynamicMapper paymentRefundAllocationDynamicMapper) {
		this.paymentRefundAllocationDynamicMapper = paymentRefundAllocationDynamicMapper;
	}

	public int create(PaymentRefundAllocationData data) {
		return paymentRefundAllocationDynamicMapper.create(data);
	}

	public int update(PaymentRefundAllocationData data, PaymentRefundAllocationQueryOptions options) {
		return paymentRefundAllocationDynamicMapper.update(data, options);
	}

	public int delete(PaymentRefundAllocationQueryOptions options) {
		return paymentRefundAllocationDynamicMapper.delete(options);
	}

	public List<PaymentRefundAllocationData> query(PaymentRefundAllocationQueryOptions options) {
		return paymentRefundAllocationDynamicMapper.query(options);
	}
}

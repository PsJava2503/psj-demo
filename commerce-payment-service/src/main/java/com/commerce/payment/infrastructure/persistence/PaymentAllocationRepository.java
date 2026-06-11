package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.PaymentAllocationQueryOptions;
import com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicMapper;
import com.commerce.payment.infrastructure.persistence.model.PaymentAllocationData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentAllocationRepository {

	private final PaymentAllocationDynamicMapper paymentAllocationDynamicMapper;

	public PaymentAllocationRepository(PaymentAllocationDynamicMapper paymentAllocationDynamicMapper) {
		this.paymentAllocationDynamicMapper = paymentAllocationDynamicMapper;
	}

	public int create(PaymentAllocationData data) {
		return paymentAllocationDynamicMapper.create(data);
	}

	public int update(PaymentAllocationData data, PaymentAllocationQueryOptions options) {
		return paymentAllocationDynamicMapper.update(data, options);
	}

	public int delete(PaymentAllocationQueryOptions options) {
		return paymentAllocationDynamicMapper.delete(options);
	}

	public List<PaymentAllocationData> query(PaymentAllocationQueryOptions options) {
		return paymentAllocationDynamicMapper.query(options);
	}
}

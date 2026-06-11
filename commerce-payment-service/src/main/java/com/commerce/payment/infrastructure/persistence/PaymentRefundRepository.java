package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.PaymentRefundQueryOptions;
import com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicMapper;
import com.commerce.payment.infrastructure.persistence.model.PaymentRefundData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRefundRepository {

	private final PaymentRefundDynamicMapper paymentRefundDynamicMapper;

	public PaymentRefundRepository(PaymentRefundDynamicMapper paymentRefundDynamicMapper) {
		this.paymentRefundDynamicMapper = paymentRefundDynamicMapper;
	}

	public int create(PaymentRefundData data) {
		return paymentRefundDynamicMapper.create(data);
	}

	public int update(PaymentRefundData data, PaymentRefundQueryOptions options) {
		return paymentRefundDynamicMapper.update(data, options);
	}

	public int delete(PaymentRefundQueryOptions options) {
		return paymentRefundDynamicMapper.delete(options);
	}

	public List<PaymentRefundData> query(PaymentRefundQueryOptions options) {
		return paymentRefundDynamicMapper.query(options);
	}
}

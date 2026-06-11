package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.PaymentNotifyLogQueryOptions;
import com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicMapper;
import com.commerce.payment.infrastructure.persistence.model.PaymentNotifyLogData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentNotifyLogRepository {

	private final PaymentNotifyLogDynamicMapper paymentNotifyLogDynamicMapper;

	public PaymentNotifyLogRepository(PaymentNotifyLogDynamicMapper paymentNotifyLogDynamicMapper) {
		this.paymentNotifyLogDynamicMapper = paymentNotifyLogDynamicMapper;
	}

	public int create(PaymentNotifyLogData data) {
		return paymentNotifyLogDynamicMapper.create(data);
	}

	public int update(PaymentNotifyLogData data, PaymentNotifyLogQueryOptions options) {
		return paymentNotifyLogDynamicMapper.update(data, options);
	}

	public int delete(PaymentNotifyLogQueryOptions options) {
		return paymentNotifyLogDynamicMapper.delete(options);
	}

	public List<PaymentNotifyLogData> query(PaymentNotifyLogQueryOptions options) {
		return paymentNotifyLogDynamicMapper.query(options);
	}
}

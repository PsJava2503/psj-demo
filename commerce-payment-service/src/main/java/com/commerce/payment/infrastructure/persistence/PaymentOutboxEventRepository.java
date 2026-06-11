package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.PaymentOutboxEventQueryOptions;
import com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicMapper;
import com.commerce.payment.infrastructure.persistence.model.PaymentOutboxEventData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentOutboxEventRepository {

	private final PaymentOutboxEventDynamicMapper paymentOutboxEventDynamicMapper;

	public PaymentOutboxEventRepository(PaymentOutboxEventDynamicMapper paymentOutboxEventDynamicMapper) {
		this.paymentOutboxEventDynamicMapper = paymentOutboxEventDynamicMapper;
	}

	public int create(PaymentOutboxEventData data) {
		return paymentOutboxEventDynamicMapper.create(data);
	}

	public int update(PaymentOutboxEventData data, PaymentOutboxEventQueryOptions options) {
		return paymentOutboxEventDynamicMapper.update(data, options);
	}

	public int delete(PaymentOutboxEventQueryOptions options) {
		return paymentOutboxEventDynamicMapper.delete(options);
	}

	public List<PaymentOutboxEventData> query(PaymentOutboxEventQueryOptions options) {
		return paymentOutboxEventDynamicMapper.query(options);
	}
}

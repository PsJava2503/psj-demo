package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.PaymentReconcileRecordQueryOptions;
import com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicMapper;
import com.commerce.payment.infrastructure.persistence.model.PaymentReconcileRecordData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentReconcileRecordRepository {

	private final PaymentReconcileRecordDynamicMapper paymentReconcileRecordDynamicMapper;

	public PaymentReconcileRecordRepository(PaymentReconcileRecordDynamicMapper paymentReconcileRecordDynamicMapper) {
		this.paymentReconcileRecordDynamicMapper = paymentReconcileRecordDynamicMapper;
	}

	public int create(PaymentReconcileRecordData data) {
		return paymentReconcileRecordDynamicMapper.create(data);
	}

	public int update(PaymentReconcileRecordData data, PaymentReconcileRecordQueryOptions options) {
		return paymentReconcileRecordDynamicMapper.update(data, options);
	}

	public int delete(PaymentReconcileRecordQueryOptions options) {
		return paymentReconcileRecordDynamicMapper.delete(options);
	}

	public List<PaymentReconcileRecordData> query(PaymentReconcileRecordQueryOptions options) {
		return paymentReconcileRecordDynamicMapper.query(options);
	}
}

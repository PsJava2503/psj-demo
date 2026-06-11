package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.PaymentOrderQueryOptions;
import com.commerce.payment.domain.model.PaymentOrderStatus;
import com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicMapper;
import com.commerce.payment.infrastructure.persistence.model.PaymentOrderData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {

	private final PaymentOrderDynamicMapper paymentOrderDynamicMapper;

	public PaymentRepository(PaymentOrderDynamicMapper paymentOrderDynamicMapper) {
		this.paymentOrderDynamicMapper = paymentOrderDynamicMapper;
	}

	public int create(PaymentOrderData data) {
		return paymentOrderDynamicMapper.create(data);
	}

	public int update(PaymentOrderData data, PaymentOrderQueryOptions options) {
		return paymentOrderDynamicMapper.update(data, options);
	}

	public int delete(PaymentOrderQueryOptions options) {
		return paymentOrderDynamicMapper.delete(options);
	}

	public List<PaymentOrderEntity> query(PaymentOrderQueryOptions options) {
		return paymentOrderDynamicMapper.query(options).stream()
				.map(this::toEntity)
				.toList();
	}

	private PaymentOrderEntity toEntity(PaymentOrderData data) {
		return new PaymentOrderEntity(
				data.id(),
				data.checkoutOrderId(),
				data.outTradeNo(),
				data.tradeNo(),
				data.amount(),
				data.subject(),
				PaymentOrderStatus.valueOf(data.status()),
				data.createTime(),
				data.updateTime()
		);
	}
}

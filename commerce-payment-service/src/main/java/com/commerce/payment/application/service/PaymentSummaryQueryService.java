package com.commerce.payment.application.service;

import com.commerce.payment.PaymentAllocationResponse;
import com.commerce.payment.PaymentOrderSummaryResponse;
import com.commerce.payment.domain.model.PaymentAllocationQueryOptions;
import com.commerce.payment.domain.model.PaymentOrderQueryOptions;
import com.commerce.payment.infrastructure.persistence.PaymentAllocationRepository;
import com.commerce.payment.infrastructure.persistence.PaymentOrderEntity;
import com.commerce.payment.infrastructure.persistence.PaymentRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PaymentSummaryQueryService {

	private final PaymentRepository paymentRepository;
	private final PaymentAllocationRepository paymentAllocationRepository;

	public PaymentSummaryQueryService(PaymentRepository paymentRepository, PaymentAllocationRepository paymentAllocationRepository) {
		this.paymentRepository = paymentRepository;
		this.paymentAllocationRepository = paymentAllocationRepository;
	}

	public PaymentOrderSummaryResponse summary(Long checkoutOrderId) {
		Optional<PaymentOrderEntity> paymentOrder = paymentRepository.query(new PaymentOrderQueryOptions(
				Optional.empty(),
				Optional.ofNullable(checkoutOrderId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		)).stream().findFirst();
		if (paymentOrder.isEmpty()) {
			return new PaymentOrderSummaryResponse(checkoutOrderId, null, null, null, null, java.util.List.of());
		}
		PaymentOrderEntity order = paymentOrder.get();
		return new PaymentOrderSummaryResponse(
				order.checkoutOrderId(),
				order.outTradeNo(),
				order.tradeNo(),
				order.amount(),
				order.status().name(),
				paymentAllocationRepository.query(new PaymentAllocationQueryOptions(
						Optional.empty(),
						Optional.ofNullable(order.id()),
						Optional.empty(),
						Optional.empty(),
						Optional.empty()
				)).stream()
						.map(allocation -> new PaymentAllocationResponse(
								allocation.subOrderId(),
								allocation.merchantId(),
								allocation.goodsAmount(),
								allocation.shippingAmount(),
								allocation.platformDiscountAmount(),
								allocation.merchantDiscountAmount(),
								allocation.paidAmount(),
								allocation.settleAmount()
						))
						.toList()
		);
	}
}

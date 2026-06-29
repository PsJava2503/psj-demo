package com.commerce.order.interfaces.graphql;

import com.commerce.order.application.port.PaymentCommandPort;
import com.commerce.order.domain.model.OrderSubOrder;
import com.commerce.order.infrastructure.persistence.OrderSubOrderRepository;
import com.commerce.payment.PaymentAllocationResponse;
import com.commerce.payment.PaymentOrderSummaryResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class OrderProductsPaymentGraphQlController {

	private final OrderSubOrderRepository orderSubOrderRepository;
	private final PaymentCommandPort paymentCommandPort;

	public OrderProductsPaymentGraphQlController(
			OrderSubOrderRepository orderSubOrderRepository,
			PaymentCommandPort paymentCommandPort
	) {
		this.orderSubOrderRepository = orderSubOrderRepository;
		this.paymentCommandPort = paymentCommandPort;
	}

	@QueryMapping
	public OrderProductsPayment orderProductsPayment(@Argument Long orderId) {
		List<OrderSubOrder> subOrders = orderSubOrderRepository.findByCheckoutOrderId(orderId);
		PaymentOrderSummaryResponse paymentSummary = paymentCommandPort.summary(orderId);
		Map<Long, PaymentAllocationResponse> allocationBySubOrderId = paymentSummary.allocations().stream()
				.filter(allocation -> allocation.subOrderId() != null)
				.collect(Collectors.toMap(PaymentAllocationResponse::subOrderId, Function.identity(), (left, right) -> right));
		return new OrderProductsPayment(
				orderId,
				new OrderPaymentSummary(
						paymentSummary.checkoutOrderId(),
						paymentSummary.outTradeNo(),
						paymentSummary.tradeNo(),
						amount(paymentSummary.amount()),
						paymentSummary.status()
				),
				subOrders.stream()
						.map(subOrder -> toProductPayment(subOrder, allocationBySubOrderId.get(subOrder.id())))
						.toList()
		);
	}

	private OrderProductPayment toProductPayment(OrderSubOrder subOrder, PaymentAllocationResponse allocation) {
		return new OrderProductPayment(
				subOrder.id(),
				subOrder.productId(),
				subOrder.skuId(),
				subOrder.productName(),
				amount(subOrder.unitPrice()),
				subOrder.quantity(),
				amount(subOrder.amount()),
				subOrder.status(),
				subOrder.refundStatus(),
				allocation == null ? null : new ProductPaymentAllocation(
						allocation.subOrderId(),
						allocation.merchantId(),
						amount(allocation.goodsAmount()),
						amount(allocation.shippingAmount()),
						amount(allocation.platformDiscountAmount()),
						amount(allocation.merchantDiscountAmount()),
						amount(allocation.paidAmount()),
						amount(allocation.settleAmount())
				)
		);
	}

	private String amount(BigDecimal amount) {
		return amount == null ? null : amount.toPlainString();
	}

	public record OrderProductsPayment(Long orderId, OrderPaymentSummary payment, List<OrderProductPayment> products) {
	}

	public record OrderPaymentSummary(
			Long checkoutOrderId,
			String outTradeNo,
			String tradeNo,
			String amount,
			String status
	) {
	}

	public record OrderProductPayment(
			Long subOrderId,
			Long productId,
			Long skuId,
			String productName,
			String unitPrice,
			Integer quantity,
			String amount,
			String status,
			String refundStatus,
			ProductPaymentAllocation payment
	) {
	}

	public record ProductPaymentAllocation(
			Long subOrderId,
			Long merchantId,
			String goodsAmount,
			String shippingAmount,
			String platformDiscountAmount,
			String merchantDiscountAmount,
			String paidAmount,
			String settleAmount
	) {
	}
}

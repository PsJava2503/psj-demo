package com.psj.commerce.order.controller;

import com.psj.commerce.api.InventoryRpcService;
import com.psj.commerce.api.NotificationRpcService;
import com.psj.commerce.api.PaymentRpcService;
import com.psj.commerce.api.ProductRpcService;
import com.psj.commerce.api.UserRpcService;
import java.math.BigDecimal;
import java.time.Instant;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@DubboReference(check = false)
	private UserRpcService userRpcService;

	@DubboReference(check = false)
	private ProductRpcService productRpcService;

	@DubboReference(check = false)
	private InventoryRpcService inventoryRpcService;

	@DubboReference(check = false)
	private PaymentRpcService paymentRpcService;

	@DubboReference(check = false)
	private NotificationRpcService notificationRpcService;

	@PostMapping
	public String create(@RequestBody CreateOrderRequest request) {
		Long orderId = Instant.now().toEpochMilli();
		String userName = userRpcService.getUserName(request.userId());
		BigDecimal price = productRpcService.getPrice(request.productId());
		BigDecimal amount = price.multiply(BigDecimal.valueOf(request.quantity()));

		if (!inventoryRpcService.deductStock(request.productId(), request.quantity())) {
			return "Order " + orderId + " failed: insufficient stock";
		}

		String paymentStatus = paymentRpcService.pay(orderId, amount);
		notificationRpcService.notifyOrderPaid(orderId);
		return "Order " + orderId + " created for " + userName + ", amount=" + amount + ", payment=" + paymentStatus;
	}

}

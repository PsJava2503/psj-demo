package com.commerce.order.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Order {

	private final Long orderId;
	private final String orderNo;
	private final Long userId;
	private final Long productId;
	private final Long skuId;
	private final String productName;
	private final BigDecimal unitPrice;
	private final Integer quantity;
	private final BigDecimal amount;
	private final Long addressId;
	private final String recipientName;
	private final String recipientPhone;
	private final String province;
	private final String city;
	private final String district;
	private final String addressDetail;
	private final Long version;
	private final ZonedDateTime createTime;
	private final ZonedDateTime updateTime;
	private OrderStatus status;

	public Order(
			Long orderId,
			String orderNo,
			Long userId,
			Long productId,
			Long skuId,
			String productName,
			BigDecimal unitPrice,
			Integer quantity,
			BigDecimal amount,
			Long addressId,
			String recipientName,
			String recipientPhone,
			String province,
			String city,
			String district,
			String addressDetail,
			Long version,
			OrderStatus status,
			ZonedDateTime createTime,
			ZonedDateTime updateTime
	) {
		this.orderId = orderId;
		this.orderNo = orderNo;
		this.userId = userId;
		this.productId = productId;
		this.skuId = skuId;
		this.productName = productName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.amount = amount;
		this.addressId = addressId;
		this.recipientName = recipientName;
		this.recipientPhone = recipientPhone;
		this.province = province;
		this.city = city;
		this.district = district;
		this.addressDetail = addressDetail;
		this.version = version == null ? 0L : version;
		this.status = status == null ? OrderStatus.CREATED : status;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public void markStockDeducted() {
		this.status = OrderStatus.STOCK_DEDUCTED;
	}

	public void markStockReserved() {
		this.status = OrderStatus.STOCK_RESERVED;
	}

	public void waitPay() {
		this.status = OrderStatus.WAIT_PAY;
	}

	public void markPaid() {
		this.status = OrderStatus.PAID;
	}

	public void markInventoryConfirming() {
		this.status = OrderStatus.INVENTORY_CONFIRMING;
	}

	public void markInventoryConfirmFailed() {
		this.status = OrderStatus.INVENTORY_CONFIRM_FAILED;
	}

	public void waitShip() {
		this.status = OrderStatus.WAIT_SHIP;
	}

	public void ship() {
		this.status = OrderStatus.SHIPPED;
	}

	public void receive() {
		this.status = OrderStatus.RECEIVED;
	}

	public void complete() {
		this.status = OrderStatus.COMPLETED;
	}

	public void fail() {
		this.status = OrderStatus.FAILED;
	}

	public void cancel() {
		this.status = OrderStatus.CANCELLED;
	}

	public void requireRefund() {
		this.status = OrderStatus.REFUND_REQUIRED;
	}

	public void markRefunded() {
		this.status = OrderStatus.REFUNDED;
	}

	public Long orderId() {
		return orderId;
	}

	public String orderNo() {
		return orderNo;
	}

	public Long userId() {
		return userId;
	}

	public Long productId() {
		return productId;
	}

	public Long skuId() {
		return skuId;
	}

	public String productName() {
		return productName;
	}

	public BigDecimal unitPrice() {
		return unitPrice;
	}

	public Integer quantity() {
		return quantity;
	}

	public BigDecimal amount() {
		return amount;
	}

	public OrderStatus status() {
		return status;
	}

	public Long addressId() {
		return addressId;
	}

	public String recipientName() {
		return recipientName;
	}

	public String recipientPhone() {
		return recipientPhone;
	}

	public String province() {
		return province;
	}

	public String city() {
		return city;
	}

	public String district() {
		return district;
	}

	public String addressDetail() {
		return addressDetail;
	}

	public Long version() {
		return version;
	}

	public ZonedDateTime createTime() {
		return createTime;
	}

	public ZonedDateTime updateTime() {
		return updateTime;
	}

}

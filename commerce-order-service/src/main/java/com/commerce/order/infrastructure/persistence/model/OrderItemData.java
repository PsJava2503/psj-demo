package com.commerce.order.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class OrderItemData {

	private Long id;
	private Long orderId;
	private Long productId;
	private Long skuId;
	private String productName;
	private BigDecimal unitPrice;
	private Integer quantity;
	private BigDecimal amount;
	private ZonedDateTime createTime;

	public OrderItemData() {
	}

	public OrderItemData(Long id, Long orderId, Long productId, Long skuId, String productName, BigDecimal unitPrice, Integer quantity, BigDecimal amount, ZonedDateTime createTime) {
		this.id = id;
		this.orderId = orderId;
		this.productId = productId;
		this.skuId = skuId;
		this.productName = productName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.amount = amount;
		this.createTime = createTime;
	}

	public Long id() {
		return id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long orderId() {
		return orderId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long productId() {
		return productId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long skuId() {
		return skuId;
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public String productName() {
		return productName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal unitPrice() {
		return unitPrice;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer quantity() {
		return quantity;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal amount() {
		return amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public ZonedDateTime createTime() {
		return createTime;
	}

	public ZonedDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}

}

package com.commerce.cart.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CartItemData {

	private Long id;
	private Long userId;
	private Long productId;
	private Long skuId;
	private String productName;
	private BigDecimal unitPrice;
	private Integer quantity;
	private Boolean selected;
	private Boolean deleted;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public CartItemData() {
	}

	public CartItemData(Long id, Long userId, Long productId, Long skuId, String productName, BigDecimal unitPrice, Integer quantity, Boolean selected, Boolean deleted, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.skuId = skuId;
		this.productName = productName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.selected = selected;
		this.deleted = deleted;
		this.createTime = createTime;
		this.updateTime = updateTime;
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

	public Long userId() {
		return userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public Boolean selected() {
		return selected;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean deleted() {
		return deleted;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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

	public ZonedDateTime updateTime() {
		return updateTime;
	}

	public ZonedDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(ZonedDateTime updateTime) {
		this.updateTime = updateTime;
	}
}

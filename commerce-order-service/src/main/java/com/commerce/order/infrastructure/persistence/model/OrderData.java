package com.commerce.order.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class OrderData {

	private Long id;
	private String orderNo;
	private Long userId;
	private Long productId;
	private Long skuId;
	private String productName;
	private BigDecimal unitPrice;
	private Integer quantity;
	private BigDecimal amount;
	private Long addressId;
	private String recipientName;
	private String recipientPhone;
	private String province;
	private String city;
	private String district;
	private String addressDetail;
	private String status;
	private Long version;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public OrderData() {
	}

	public OrderData(Long id, String orderNo, Long userId, Long productId, Long skuId, String productName, BigDecimal unitPrice, Integer quantity, BigDecimal amount, Long addressId, String recipientName, String recipientPhone, String province, String city, String district, String addressDetail, String status, Long version, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
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
		this.status = status;
		this.version = version;
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

	public String orderNo() {
		return orderNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public BigDecimal amount() {
		return amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long addressId() {
		return addressId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public String recipientName() {
		return recipientName;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String recipientPhone() {
		return recipientPhone;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipientPhone) {
		this.recipientPhone = recipientPhone;
	}

	public String province() {
		return province;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String city() {
		return city;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String district() {
		return district;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String addressDetail() {
		return addressDetail;
	}

	public String getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}

	public String status() {
		return status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long version() {
		return version;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
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

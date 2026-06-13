package com.commerce.payment.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PaymentAllocationData {

	private Long id;
	private Long paymentOrderId;
	private Long checkoutOrderId;
	private Long subOrderId;
	private Long merchantId;
	private BigDecimal goodsAmount;
	private BigDecimal shippingAmount;
	private BigDecimal platformDiscountAmount;
	private BigDecimal merchantDiscountAmount;
	private BigDecimal paidAmount;
	private BigDecimal settleAmount;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public PaymentAllocationData() {
	}

	public PaymentAllocationData(Long id, Long paymentOrderId, Long checkoutOrderId, Long subOrderId, Long merchantId, BigDecimal goodsAmount, BigDecimal shippingAmount, BigDecimal platformDiscountAmount, BigDecimal merchantDiscountAmount, BigDecimal paidAmount, BigDecimal settleAmount, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.paymentOrderId = paymentOrderId;
		this.checkoutOrderId = checkoutOrderId;
		this.subOrderId = subOrderId;
		this.merchantId = merchantId;
		this.goodsAmount = goodsAmount;
		this.shippingAmount = shippingAmount;
		this.platformDiscountAmount = platformDiscountAmount;
		this.merchantDiscountAmount = merchantDiscountAmount;
		this.paidAmount = paidAmount;
		this.settleAmount = settleAmount;
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

	public Long paymentOrderId() {
		return paymentOrderId;
	}

	public Long getPaymentOrderId() {
		return paymentOrderId;
	}

	public void setPaymentOrderId(Long paymentOrderId) {
		this.paymentOrderId = paymentOrderId;
	}

	public Long checkoutOrderId() {
		return checkoutOrderId;
	}

	public Long getCheckoutOrderId() {
		return checkoutOrderId;
	}

	public void setCheckoutOrderId(Long checkoutOrderId) {
		this.checkoutOrderId = checkoutOrderId;
	}

	public Long subOrderId() {
		return subOrderId;
	}

	public Long getSubOrderId() {
		return subOrderId;
	}

	public void setSubOrderId(Long subOrderId) {
		this.subOrderId = subOrderId;
	}

	public Long merchantId() {
		return merchantId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public BigDecimal goodsAmount() {
		return goodsAmount;
	}

	public BigDecimal getGoodsAmount() {
		return goodsAmount;
	}

	public void setGoodsAmount(BigDecimal goodsAmount) {
		this.goodsAmount = goodsAmount;
	}

	public BigDecimal shippingAmount() {
		return shippingAmount;
	}

	public BigDecimal getShippingAmount() {
		return shippingAmount;
	}

	public void setShippingAmount(BigDecimal shippingAmount) {
		this.shippingAmount = shippingAmount;
	}

	public BigDecimal platformDiscountAmount() {
		return platformDiscountAmount;
	}

	public BigDecimal getPlatformDiscountAmount() {
		return platformDiscountAmount;
	}

	public void setPlatformDiscountAmount(BigDecimal platformDiscountAmount) {
		this.platformDiscountAmount = platformDiscountAmount;
	}

	public BigDecimal merchantDiscountAmount() {
		return merchantDiscountAmount;
	}

	public BigDecimal getMerchantDiscountAmount() {
		return merchantDiscountAmount;
	}

	public void setMerchantDiscountAmount(BigDecimal merchantDiscountAmount) {
		this.merchantDiscountAmount = merchantDiscountAmount;
	}

	public BigDecimal paidAmount() {
		return paidAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal settleAmount() {
		return settleAmount;
	}

	public BigDecimal getSettleAmount() {
		return settleAmount;
	}

	public void setSettleAmount(BigDecimal settleAmount) {
		this.settleAmount = settleAmount;
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

package com.commerce.payment.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PaymentRefundAllocationData {

	private Long id;
	private Long paymentRefundId;
	private Long paymentAllocationId;
	private Long checkoutOrderId;
	private Long subOrderId;
	private Long merchantId;
	private BigDecimal refundGoodsAmount;
	private BigDecimal refundShippingAmount;
	private BigDecimal refundPlatformDiscountAmount;
	private BigDecimal refundMerchantDiscountAmount;
	private BigDecimal refundPaidAmount;
	private ZonedDateTime createTime;

	public PaymentRefundAllocationData() {
	}

	public PaymentRefundAllocationData(Long id, Long paymentRefundId, Long paymentAllocationId, Long checkoutOrderId, Long subOrderId, Long merchantId, BigDecimal refundGoodsAmount, BigDecimal refundShippingAmount, BigDecimal refundPlatformDiscountAmount, BigDecimal refundMerchantDiscountAmount, BigDecimal refundPaidAmount, ZonedDateTime createTime) {
		this.id = id;
		this.paymentRefundId = paymentRefundId;
		this.paymentAllocationId = paymentAllocationId;
		this.checkoutOrderId = checkoutOrderId;
		this.subOrderId = subOrderId;
		this.merchantId = merchantId;
		this.refundGoodsAmount = refundGoodsAmount;
		this.refundShippingAmount = refundShippingAmount;
		this.refundPlatformDiscountAmount = refundPlatformDiscountAmount;
		this.refundMerchantDiscountAmount = refundMerchantDiscountAmount;
		this.refundPaidAmount = refundPaidAmount;
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

	public Long paymentRefundId() {
		return paymentRefundId;
	}

	public Long getPaymentRefundId() {
		return paymentRefundId;
	}

	public void setPaymentRefundId(Long paymentRefundId) {
		this.paymentRefundId = paymentRefundId;
	}

	public Long paymentAllocationId() {
		return paymentAllocationId;
	}

	public Long getPaymentAllocationId() {
		return paymentAllocationId;
	}

	public void setPaymentAllocationId(Long paymentAllocationId) {
		this.paymentAllocationId = paymentAllocationId;
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

	public BigDecimal refundGoodsAmount() {
		return refundGoodsAmount;
	}

	public BigDecimal getRefundGoodsAmount() {
		return refundGoodsAmount;
	}

	public void setRefundGoodsAmount(BigDecimal refundGoodsAmount) {
		this.refundGoodsAmount = refundGoodsAmount;
	}

	public BigDecimal refundShippingAmount() {
		return refundShippingAmount;
	}

	public BigDecimal getRefundShippingAmount() {
		return refundShippingAmount;
	}

	public void setRefundShippingAmount(BigDecimal refundShippingAmount) {
		this.refundShippingAmount = refundShippingAmount;
	}

	public BigDecimal refundPlatformDiscountAmount() {
		return refundPlatformDiscountAmount;
	}

	public BigDecimal getRefundPlatformDiscountAmount() {
		return refundPlatformDiscountAmount;
	}

	public void setRefundPlatformDiscountAmount(BigDecimal refundPlatformDiscountAmount) {
		this.refundPlatformDiscountAmount = refundPlatformDiscountAmount;
	}

	public BigDecimal refundMerchantDiscountAmount() {
		return refundMerchantDiscountAmount;
	}

	public BigDecimal getRefundMerchantDiscountAmount() {
		return refundMerchantDiscountAmount;
	}

	public void setRefundMerchantDiscountAmount(BigDecimal refundMerchantDiscountAmount) {
		this.refundMerchantDiscountAmount = refundMerchantDiscountAmount;
	}

	public BigDecimal refundPaidAmount() {
		return refundPaidAmount;
	}

	public BigDecimal getRefundPaidAmount() {
		return refundPaidAmount;
	}

	public void setRefundPaidAmount(BigDecimal refundPaidAmount) {
		this.refundPaidAmount = refundPaidAmount;
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

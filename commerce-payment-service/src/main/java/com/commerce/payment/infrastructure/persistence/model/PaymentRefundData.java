package com.commerce.payment.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PaymentRefundData {

	private Long id;
	private Long paymentOrderId;
	private Long checkoutOrderId;
	private String outTradeNo;
	private String outRefundNo;
	private BigDecimal refundAmount;
	private String status;
	private String rawResponse;
	private ZonedDateTime createTime;

	public PaymentRefundData() {
	}

	public PaymentRefundData(Long id, Long paymentOrderId, Long checkoutOrderId, String outTradeNo, String outRefundNo, BigDecimal refundAmount, String status, String rawResponse, ZonedDateTime createTime) {
		this.id = id;
		this.paymentOrderId = paymentOrderId;
		this.checkoutOrderId = checkoutOrderId;
		this.outTradeNo = outTradeNo;
		this.outRefundNo = outRefundNo;
		this.refundAmount = refundAmount;
		this.status = status;
		this.rawResponse = rawResponse;
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

	public String outTradeNo() {
		return outTradeNo;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String outRefundNo() {
		return outRefundNo;
	}

	public String getOutRefundNo() {
		return outRefundNo;
	}

	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}

	public BigDecimal refundAmount() {
		return refundAmount;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
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

	public String rawResponse() {
		return rawResponse;
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
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

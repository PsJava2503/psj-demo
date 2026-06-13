package com.commerce.payment.infrastructure.persistence.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PaymentOrderData {

	private Long id;
	private Long checkoutOrderId;
	private String outTradeNo;
	private String tradeNo;
	private BigDecimal amount;
	private String subject;
	private String status;
	private String rawResponse;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public PaymentOrderData() {
	}

	public PaymentOrderData(Long id, Long checkoutOrderId, String outTradeNo, String tradeNo, BigDecimal amount, String subject, String status, String rawResponse, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.checkoutOrderId = checkoutOrderId;
		this.outTradeNo = outTradeNo;
		this.tradeNo = tradeNo;
		this.amount = amount;
		this.subject = subject;
		this.status = status;
		this.rawResponse = rawResponse;
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

	public String tradeNo() {
		return tradeNo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
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

	public String subject() {
		return subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

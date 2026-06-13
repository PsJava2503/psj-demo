package com.commerce.payment.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class PaymentNotifyLogData {

	private Long id;
	private String notifyId;
	private String outTradeNo;
	private String tradeStatus;
	private Boolean verified;
	private String payload;
	private ZonedDateTime createTime;

	public PaymentNotifyLogData() {
	}

	public PaymentNotifyLogData(Long id, String notifyId, String outTradeNo, String tradeStatus, Boolean verified, String payload, ZonedDateTime createTime) {
		this.id = id;
		this.notifyId = notifyId;
		this.outTradeNo = outTradeNo;
		this.tradeStatus = tradeStatus;
		this.verified = verified;
		this.payload = payload;
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

	public String notifyId() {
		return notifyId;
	}

	public String getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(String notifyId) {
		this.notifyId = notifyId;
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

	public String tradeStatus() {
		return tradeStatus;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public Boolean verified() {
		return verified;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public String payload() {
		return payload;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
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

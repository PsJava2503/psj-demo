package com.commerce.notification.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class NotificationRecordData {

	private Long id;
	private Long orderId;
	private Long userId;
	private String templateCode;
	private String channel;
	private String recipient;
	private String payload;
	private String status;
	private String idempotencyKey;
	private ZonedDateTime sentTime;
	private String errorMessage;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public NotificationRecordData() {
	}

	public NotificationRecordData(Long id, Long orderId, Long userId, String templateCode, String channel, String recipient, String payload, String status, String idempotencyKey, ZonedDateTime sentTime, String errorMessage, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.orderId = orderId;
		this.userId = userId;
		this.templateCode = templateCode;
		this.channel = channel;
		this.recipient = recipient;
		this.payload = payload;
		this.status = status;
		this.idempotencyKey = idempotencyKey;
		this.sentTime = sentTime;
		this.errorMessage = errorMessage;
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

	public Long orderId() {
		return orderId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
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

	public String templateCode() {
		return templateCode;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String channel() {
		return channel;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String recipient() {
		return recipient;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
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

	public String status() {
		return status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String idempotencyKey() {
		return idempotencyKey;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public ZonedDateTime sentTime() {
		return sentTime;
	}

	public ZonedDateTime getSentTime() {
		return sentTime;
	}

	public void setSentTime(ZonedDateTime sentTime) {
		this.sentTime = sentTime;
	}

	public String errorMessage() {
		return errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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

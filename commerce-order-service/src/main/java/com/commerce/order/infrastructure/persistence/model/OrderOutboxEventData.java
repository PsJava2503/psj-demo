package com.commerce.order.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class OrderOutboxEventData {

	private Long id;
	private String eventKey;
	private String eventType;
	private Long aggregateId;
	private String payload;
	private String status;
	private Integer attemptCount;
	private String lastError;
	private ZonedDateTime nextRetryTime;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public OrderOutboxEventData() {
	}

	public OrderOutboxEventData(Long id, String eventKey, String eventType, Long aggregateId, String payload, String status, Integer attemptCount, String lastError, ZonedDateTime nextRetryTime, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.eventKey = eventKey;
		this.eventType = eventType;
		this.aggregateId = aggregateId;
		this.payload = payload;
		this.status = status;
		this.attemptCount = attemptCount;
		this.lastError = lastError;
		this.nextRetryTime = nextRetryTime;
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

	public String eventKey() {
		return eventKey;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String eventType() {
		return eventType;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Long aggregateId() {
		return aggregateId;
	}

	public Long getAggregateId() {
		return aggregateId;
	}

	public void setAggregateId(Long aggregateId) {
		this.aggregateId = aggregateId;
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

	public Integer attemptCount() {
		return attemptCount;
	}

	public Integer getAttemptCount() {
		return attemptCount;
	}

	public void setAttemptCount(Integer attemptCount) {
		this.attemptCount = attemptCount;
	}

	public String lastError() {
		return lastError;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	public ZonedDateTime nextRetryTime() {
		return nextRetryTime;
	}

	public ZonedDateTime getNextRetryTime() {
		return nextRetryTime;
	}

	public void setNextRetryTime(ZonedDateTime nextRetryTime) {
		this.nextRetryTime = nextRetryTime;
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

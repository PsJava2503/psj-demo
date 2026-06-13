package com.commerce.payment.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class IdempotencyRecordData {

	private Long id;
	private String idempotencyKey;
	private ZonedDateTime createTime;

	public IdempotencyRecordData() {
	}

	public IdempotencyRecordData(Long id, String idempotencyKey, ZonedDateTime createTime) {
		this.id = id;
		this.idempotencyKey = idempotencyKey;
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

	public String idempotencyKey() {
		return idempotencyKey;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
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

package com.commerce.order.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class OrderStatusLogData {

	private Long id;
	private Long orderId;
	private String fromStatus;
	private String toStatus;
	private String reason;
	private ZonedDateTime createTime;

	public OrderStatusLogData() {
	}

	public OrderStatusLogData(Long id, Long orderId, String fromStatus, String toStatus, String reason, ZonedDateTime createTime) {
		this.id = id;
		this.orderId = orderId;
		this.fromStatus = fromStatus;
		this.toStatus = toStatus;
		this.reason = reason;
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

	public Long orderId() {
		return orderId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String fromStatus() {
		return fromStatus;
	}

	public String getFromStatus() {
		return fromStatus;
	}

	public void setFromStatus(String fromStatus) {
		this.fromStatus = fromStatus;
	}

	public String toStatus() {
		return toStatus;
	}

	public String getToStatus() {
		return toStatus;
	}

	public void setToStatus(String toStatus) {
		this.toStatus = toStatus;
	}

	public String reason() {
		return reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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

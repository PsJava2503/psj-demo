package com.commerce.order.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class OrderInventoryReservationData {

	private Long id;
	private Long orderId;
	private Long reservationId;
	private String status;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public OrderInventoryReservationData() {
	}

	public OrderInventoryReservationData(Long id, Long orderId, Long reservationId, String status, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.orderId = orderId;
		this.reservationId = reservationId;
		this.status = status;
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

	public Long reservationId() {
		return reservationId;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
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

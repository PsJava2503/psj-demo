package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class UserAddressSlotData {

	private Long id;
	private Long userId;
	private Long addressId;
	private String slotName;
	private Boolean deleted;
	private ZonedDateTime createTime;

	public UserAddressSlotData() {
	}

	public UserAddressSlotData(Long id, Long userId, Long addressId, String slotName, Boolean deleted, ZonedDateTime createTime) {
		this.id = id;
		this.userId = userId;
		this.addressId = addressId;
		this.slotName = slotName;
		this.deleted = deleted;
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

	public Long userId() {
		return userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long addressId() {
		return addressId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public String slotName() {
		return slotName;
	}

	public String getSlotName() {
		return slotName;
	}

	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}

	public Boolean deleted() {
		return deleted;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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

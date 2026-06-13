package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class UserData {

	private Long id;
	private String firstName;
	private String secondName;
	private String phone;
	private String email;
	private Long defaultAddressSlotId;
	private Boolean deleted;
	private Boolean enabled;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public UserData() {
	}

	public UserData(Long id, String firstName, String secondName, String phone, String email, Long defaultAddressSlotId, Boolean deleted, Boolean enabled, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.firstName = firstName;
		this.secondName = secondName;
		this.phone = phone;
		this.email = email;
		this.defaultAddressSlotId = defaultAddressSlotId;
		this.deleted = deleted;
		this.enabled = enabled;
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

	public String firstName() {
		return firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String secondName() {
		return secondName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String phone() {
		return phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String email() {
		return email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long defaultAddressSlotId() {
		return defaultAddressSlotId;
	}

	public Long getDefaultAddressSlotId() {
		return defaultAddressSlotId;
	}

	public void setDefaultAddressSlotId(Long defaultAddressSlotId) {
		this.defaultAddressSlotId = defaultAddressSlotId;
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

	public Boolean enabled() {
		return enabled;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
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

package com.commerce.address.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class AddressData {

	private Long id;
	private Long userId;
	private String recipientName;
	private String phone;
	private String province;
	private String city;
	private String district;
	private String detail;
	private Boolean defaultAddress;
	private Boolean deleted;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public AddressData() {
	}

	public AddressData(Long id, Long userId, String recipientName, String phone, String province, String city, String district, String detail, Boolean defaultAddress, Boolean deleted, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.userId = userId;
		this.recipientName = recipientName;
		this.phone = phone;
		this.province = province;
		this.city = city;
		this.district = district;
		this.detail = detail;
		this.defaultAddress = defaultAddress;
		this.deleted = deleted;
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

	public Long userId() {
		return userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String recipientName() {
		return recipientName;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
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

	public String province() {
		return province;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String city() {
		return city;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String district() {
		return district;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String detail() {
		return detail;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Boolean defaultAddress() {
		return defaultAddress;
	}

	public Boolean getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(Boolean defaultAddress) {
		this.defaultAddress = defaultAddress;
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

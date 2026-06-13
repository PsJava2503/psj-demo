package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class UserCredentialData {

	private Long userId;
	private String username;
	private String passwordHash;
	private String passwordSalt;
	private String passwordAlgorithm;
	private Boolean enabled;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public UserCredentialData() {
	}

	public UserCredentialData(Long userId, String username, String passwordHash, String passwordSalt, String passwordAlgorithm, Boolean enabled, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.userId = userId;
		this.username = username;
		this.passwordHash = passwordHash;
		this.passwordSalt = passwordSalt;
		this.passwordAlgorithm = passwordAlgorithm;
		this.enabled = enabled;
		this.createTime = createTime;
		this.updateTime = updateTime;
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

	public String username() {
		return username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String passwordHash() {
		return passwordHash;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String passwordSalt() {
		return passwordSalt;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public String passwordAlgorithm() {
		return passwordAlgorithm;
	}

	public String getPasswordAlgorithm() {
		return passwordAlgorithm;
	}

	public void setPasswordAlgorithm(String passwordAlgorithm) {
		this.passwordAlgorithm = passwordAlgorithm;
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

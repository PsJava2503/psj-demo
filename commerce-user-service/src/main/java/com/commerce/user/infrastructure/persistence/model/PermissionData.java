package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class PermissionData {

	private Long id;
	private String code;
	private String resource;
	private String action;
	private String name;
	private String description;
	private Boolean enabled;
	private Boolean deleted;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;

	public PermissionData() {
	}

	public PermissionData(Long id, String code, String resource, String action, String name, String description, Boolean enabled, Boolean deleted, ZonedDateTime createTime, ZonedDateTime updateTime) {
		this.id = id;
		this.code = code;
		this.resource = resource;
		this.action = action;
		this.name = name;
		this.description = description;
		this.enabled = enabled;
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

	public String code() {
		return code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String resource() {
		return resource;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String action() {
		return action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String name() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String description() {
		return description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

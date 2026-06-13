package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class UserRoleData {

	private Long userId;
	private Long roleId;
	private ZonedDateTime assignedAt;
	private Long assignedBy;

	public UserRoleData() {
	}

	public UserRoleData(Long userId, Long roleId, ZonedDateTime assignedAt, Long assignedBy) {
		this.userId = userId;
		this.roleId = roleId;
		this.assignedAt = assignedAt;
		this.assignedBy = assignedBy;
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

	public Long roleId() {
		return roleId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public ZonedDateTime assignedAt() {
		return assignedAt;
	}

	public ZonedDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(ZonedDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}

	public Long assignedBy() {
		return assignedBy;
	}

	public Long getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Long assignedBy) {
		this.assignedBy = assignedBy;
	}

}

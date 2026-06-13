package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public class RolePermissionData {

	private Long roleId;
	private Long permissionId;
	private ZonedDateTime assignedAt;
	private Long assignedBy;

	public RolePermissionData() {
	}

	public RolePermissionData(Long roleId, Long permissionId, ZonedDateTime assignedAt, Long assignedBy) {
		this.roleId = roleId;
		this.permissionId = permissionId;
		this.assignedAt = assignedAt;
		this.assignedBy = assignedBy;
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

	public Long permissionId() {
		return permissionId;
	}

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
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

package com.commerce.user.domain.model;

import java.time.ZonedDateTime;

public record RolePermissionAssignment(Long roleId, Long permissionId, ZonedDateTime assignedAt, Long assignedBy) {
}

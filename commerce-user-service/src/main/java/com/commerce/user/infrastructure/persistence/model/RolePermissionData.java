package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record RolePermissionData(Long roleId, Long permissionId, ZonedDateTime assignedAt, Long assignedBy) {
}

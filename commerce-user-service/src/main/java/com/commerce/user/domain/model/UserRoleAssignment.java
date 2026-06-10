package com.commerce.user.domain.model;

import java.time.ZonedDateTime;

public record UserRoleAssignment(Long userId, Long roleId, ZonedDateTime assignedAt, Long assignedBy) {
}

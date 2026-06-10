package com.commerce.user.infrastructure.persistence.model;

import java.time.ZonedDateTime;

public record UserRoleData(Long userId, Long roleId, ZonedDateTime assignedAt, Long assignedBy) {
}

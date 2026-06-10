package com.commerce.user.domain.port;

import com.commerce.user.domain.model.RolePermissionAssignment;
import com.commerce.user.domain.model.RolePermissionQueryOptions;
import java.util.List;

public interface RolePermissionRepository {

	int create(RolePermissionAssignment assignment);

	int update(RolePermissionAssignment assignment);

	int delete(RolePermissionAssignment assignment);

	List<RolePermissionAssignment> query(RolePermissionQueryOptions options);

}

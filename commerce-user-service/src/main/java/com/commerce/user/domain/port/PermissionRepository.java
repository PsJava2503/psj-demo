package com.commerce.user.domain.port;

import com.commerce.user.domain.model.Permission;
import com.commerce.user.domain.model.PermissionQueryOptions;
import java.util.List;

public interface PermissionRepository {

	int create(Permission permission);

	int update(Permission permission);

	int delete(Long id);

	List<Permission> query(PermissionQueryOptions options);

}

package com.commerce.user.application.port;

import com.commerce.user.domain.model.Permission;
import com.commerce.user.domain.model.PermissionQueryOptions;
import java.util.List;

public interface PermissionUseCase {

	int create(Permission permission);

	int update(Permission permission);

	int delete(Long id);

	List<Permission> query(PermissionQueryOptions options);

}

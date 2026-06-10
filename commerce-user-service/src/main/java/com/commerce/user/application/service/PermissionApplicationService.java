package com.commerce.user.application.service;

import com.commerce.user.application.port.PermissionUseCase;
import com.commerce.user.domain.model.Permission;
import com.commerce.user.domain.model.PermissionQueryOptions;
import com.commerce.user.domain.service.PermissionDomainService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PermissionApplicationService implements PermissionUseCase {

	private final PermissionDomainService permissionDomainService;

	public PermissionApplicationService(PermissionDomainService permissionDomainService) {
		this.permissionDomainService = permissionDomainService;
	}

	@Override
	public int create(Permission permission) {
		return permissionDomainService.create(permission);
	}

	@Override
	public int update(Permission permission) {
		return permissionDomainService.update(permission);
	}

	@Override
	public int delete(Long id) {
		return permissionDomainService.delete(id);
	}

	@Override
	public List<Permission> query(PermissionQueryOptions options) {
		return permissionDomainService.query(options);
	}

}

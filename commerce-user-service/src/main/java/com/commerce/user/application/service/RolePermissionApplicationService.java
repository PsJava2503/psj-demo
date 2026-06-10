package com.commerce.user.application.service;

import com.commerce.user.application.port.RolePermissionUseCase;
import com.commerce.user.domain.model.RolePermissionAssignment;
import com.commerce.user.domain.model.RolePermissionQueryOptions;
import com.commerce.user.domain.service.RolePermissionDomainService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionApplicationService implements RolePermissionUseCase {

	private final RolePermissionDomainService rolePermissionDomainService;

	public RolePermissionApplicationService(RolePermissionDomainService rolePermissionDomainService) {
		this.rolePermissionDomainService = rolePermissionDomainService;
	}

	@Override
	public int create(RolePermissionAssignment assignment) {
		return rolePermissionDomainService.create(assignment);
	}

	@Override
	public int update(RolePermissionAssignment assignment) {
		return rolePermissionDomainService.update(assignment);
	}

	@Override
	public int delete(RolePermissionAssignment assignment) {
		return rolePermissionDomainService.delete(assignment);
	}

	@Override
	public List<RolePermissionAssignment> query(RolePermissionQueryOptions options) {
		return rolePermissionDomainService.query(options);
	}

}

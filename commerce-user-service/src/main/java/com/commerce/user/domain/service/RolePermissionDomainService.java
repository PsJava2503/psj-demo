package com.commerce.user.domain.service;

import com.commerce.user.domain.model.RolePermissionAssignment;
import com.commerce.user.domain.model.RolePermissionQueryOptions;
import com.commerce.user.domain.port.RolePermissionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionDomainService {

	private final RolePermissionRepository rolePermissionRepository;

	public RolePermissionDomainService(RolePermissionRepository rolePermissionRepository) {
		this.rolePermissionRepository = rolePermissionRepository;
	}

	public int create(RolePermissionAssignment assignment) {
		return rolePermissionRepository.create(assignment);
	}

	public int update(RolePermissionAssignment assignment) {
		return rolePermissionRepository.update(assignment);
	}

	public int delete(RolePermissionAssignment assignment) {
		return rolePermissionRepository.delete(assignment);
	}

	public List<RolePermissionAssignment> query(RolePermissionQueryOptions options) {
		return rolePermissionRepository.query(options);
	}

}

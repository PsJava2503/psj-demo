package com.commerce.user.domain.service;

import com.commerce.user.domain.model.Permission;
import com.commerce.user.domain.model.PermissionQueryOptions;
import com.commerce.user.domain.port.PermissionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PermissionDomainService {

	private final PermissionRepository permissionRepository;

	public PermissionDomainService(PermissionRepository permissionRepository) {
		this.permissionRepository = permissionRepository;
	}

	public int create(Permission permission) {
		return permissionRepository.create(permission);
	}

	public int update(Permission permission) {
		return permissionRepository.update(permission);
	}

	public int delete(Long id) {
		return permissionRepository.delete(id);
	}

	public List<Permission> query(PermissionQueryOptions options) {
		return permissionRepository.query(options);
	}

}

package com.commerce.user.application.service;

import com.commerce.user.application.port.RoleUseCase;
import com.commerce.user.domain.model.Role;
import com.commerce.user.domain.model.RoleQueryOptions;
import com.commerce.user.domain.service.RoleDomainService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleApplicationService implements RoleUseCase {

	private final RoleDomainService roleDomainService;

	public RoleApplicationService(RoleDomainService roleDomainService) {
		this.roleDomainService = roleDomainService;
	}

	@Override
	public int create(Role role) {
		return roleDomainService.create(role);
	}

	@Override
	public int update(Role role) {
		return roleDomainService.update(role);
	}

	@Override
	public int delete(Long id) {
		return roleDomainService.delete(id);
	}

	@Override
	public List<Role> query(RoleQueryOptions options) {
		return roleDomainService.query(options);
	}

}

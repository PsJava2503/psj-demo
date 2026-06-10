package com.commerce.user.application.service;

import com.commerce.user.application.port.UserRoleUseCase;
import com.commerce.user.domain.model.UserRoleAssignment;
import com.commerce.user.domain.model.UserRoleQueryOptions;
import com.commerce.user.domain.service.UserRoleDomainService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserRoleApplicationService implements UserRoleUseCase {

	private final UserRoleDomainService userRoleDomainService;

	public UserRoleApplicationService(UserRoleDomainService userRoleDomainService) {
		this.userRoleDomainService = userRoleDomainService;
	}

	@Override
	public int create(UserRoleAssignment assignment) {
		return userRoleDomainService.create(assignment);
	}

	@Override
	public int update(UserRoleAssignment assignment) {
		return userRoleDomainService.update(assignment);
	}

	@Override
	public int delete(UserRoleAssignment assignment) {
		return userRoleDomainService.delete(assignment);
	}

	@Override
	public List<UserRoleAssignment> query(UserRoleQueryOptions options) {
		return userRoleDomainService.query(options);
	}

}

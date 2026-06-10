package com.commerce.user.domain.service;

import com.commerce.user.domain.model.UserRoleAssignment;
import com.commerce.user.domain.model.UserRoleQueryOptions;
import com.commerce.user.domain.port.UserRoleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserRoleDomainService {

	private final UserRoleRepository userRoleRepository;

	public UserRoleDomainService(UserRoleRepository userRoleRepository) {
		this.userRoleRepository = userRoleRepository;
	}

	public int create(UserRoleAssignment assignment) {
		return userRoleRepository.create(assignment);
	}

	public int update(UserRoleAssignment assignment) {
		return userRoleRepository.update(assignment);
	}

	public int delete(UserRoleAssignment assignment) {
		return userRoleRepository.delete(assignment);
	}

	public List<UserRoleAssignment> query(UserRoleQueryOptions options) {
		return userRoleRepository.query(options);
	}

}

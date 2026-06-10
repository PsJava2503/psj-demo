package com.commerce.user.domain.service;

import com.commerce.user.domain.model.Role;
import com.commerce.user.domain.model.RoleQueryOptions;
import com.commerce.user.domain.port.RoleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleDomainService {

	private final RoleRepository roleRepository;

	public RoleDomainService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public int create(Role role) {
		return roleRepository.create(role);
	}

	public int update(Role role) {
		return roleRepository.update(role);
	}

	public int delete(Long id) {
		return roleRepository.delete(id);
	}

	public List<Role> query(RoleQueryOptions options) {
		return roleRepository.query(options);
	}

}

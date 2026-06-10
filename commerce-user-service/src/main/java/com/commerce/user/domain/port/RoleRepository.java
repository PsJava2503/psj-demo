package com.commerce.user.domain.port;

import com.commerce.user.domain.model.Role;
import com.commerce.user.domain.model.RoleQueryOptions;
import java.util.List;

public interface RoleRepository {

	int create(Role role);

	int update(Role role);

	int delete(Long id);

	List<Role> query(RoleQueryOptions options);

}

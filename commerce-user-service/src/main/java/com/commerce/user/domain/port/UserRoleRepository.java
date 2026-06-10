package com.commerce.user.domain.port;

import com.commerce.user.domain.model.UserRoleAssignment;
import com.commerce.user.domain.model.UserRoleQueryOptions;
import java.util.List;

public interface UserRoleRepository {

	int create(UserRoleAssignment assignment);

	int update(UserRoleAssignment assignment);

	int delete(UserRoleAssignment assignment);

	List<UserRoleAssignment> query(UserRoleQueryOptions options);

}

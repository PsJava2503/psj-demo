package com.commerce.user.application.port;

import com.commerce.user.domain.model.UserRoleAssignment;
import com.commerce.user.domain.model.UserRoleQueryOptions;
import java.util.List;

public interface UserRoleUseCase {

	int create(UserRoleAssignment assignment);

	int update(UserRoleAssignment assignment);

	int delete(UserRoleAssignment assignment);

	List<UserRoleAssignment> query(UserRoleQueryOptions options);

}

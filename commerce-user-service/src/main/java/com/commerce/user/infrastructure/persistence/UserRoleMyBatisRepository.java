package com.commerce.user.infrastructure.persistence;

import com.commerce.user.domain.model.UserRoleAssignment;
import com.commerce.user.domain.model.UserRoleQueryOptions;
import com.commerce.user.domain.port.UserRoleRepository;
import com.commerce.user.infrastructure.persistence.mapper.UserRoleDynamicMapper;
import com.commerce.user.infrastructure.persistence.model.UserRoleData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleMyBatisRepository implements UserRoleRepository {

	private final UserRoleDynamicMapper userRoleDynamicMapper;

	public UserRoleMyBatisRepository(UserRoleDynamicMapper userRoleDynamicMapper) {
		this.userRoleDynamicMapper = userRoleDynamicMapper;
	}

	@Override
	public int create(UserRoleAssignment assignment) {
		return userRoleDynamicMapper.create(toData(assignment));
	}

	@Override
	public int update(UserRoleAssignment assignment) {
		return userRoleDynamicMapper.update(toData(assignment));
	}

	@Override
	public int delete(UserRoleAssignment assignment) {
		return userRoleDynamicMapper.delete(toData(assignment));
	}

	@Override
	public List<UserRoleAssignment> query(UserRoleQueryOptions options) {
		return userRoleDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private UserRoleAssignment toDomain(UserRoleData data) {
		return new UserRoleAssignment(data.userId(), data.roleId(), data.assignedAt(), data.assignedBy());
	}

	private UserRoleData toData(UserRoleAssignment assignment) {
		return new UserRoleData(
				assignment.userId(), assignment.roleId(), assignment.assignedAt(), assignment.assignedBy()
		);
	}

}

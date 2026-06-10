package com.commerce.user.infrastructure.persistence;

import com.commerce.user.domain.model.RolePermissionAssignment;
import com.commerce.user.domain.model.RolePermissionQueryOptions;
import com.commerce.user.domain.port.RolePermissionRepository;
import com.commerce.user.infrastructure.persistence.mapper.RolePermissionDynamicMapper;
import com.commerce.user.infrastructure.persistence.model.RolePermissionData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RolePermissionMyBatisRepository implements RolePermissionRepository {

	private final RolePermissionDynamicMapper rolePermissionDynamicMapper;

	public RolePermissionMyBatisRepository(RolePermissionDynamicMapper rolePermissionDynamicMapper) {
		this.rolePermissionDynamicMapper = rolePermissionDynamicMapper;
	}

	@Override
	public int create(RolePermissionAssignment assignment) {
		return rolePermissionDynamicMapper.create(toData(assignment));
	}

	@Override
	public int update(RolePermissionAssignment assignment) {
		return rolePermissionDynamicMapper.update(toData(assignment));
	}

	@Override
	public int delete(RolePermissionAssignment assignment) {
		return rolePermissionDynamicMapper.delete(toData(assignment));
	}

	@Override
	public List<RolePermissionAssignment> query(RolePermissionQueryOptions options) {
		return rolePermissionDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private RolePermissionAssignment toDomain(RolePermissionData data) {
		return new RolePermissionAssignment(data.roleId(), data.permissionId(), data.assignedAt(), data.assignedBy());
	}

	private RolePermissionData toData(RolePermissionAssignment assignment) {
		return new RolePermissionData(
				assignment.roleId(), assignment.permissionId(), assignment.assignedAt(), assignment.assignedBy()
		);
	}

}

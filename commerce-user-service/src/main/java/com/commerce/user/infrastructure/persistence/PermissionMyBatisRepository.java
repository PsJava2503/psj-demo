package com.commerce.user.infrastructure.persistence;

import com.commerce.user.domain.model.Permission;
import com.commerce.user.domain.model.PermissionQueryOptions;
import com.commerce.user.domain.port.PermissionRepository;
import com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicMapper;
import com.commerce.user.infrastructure.persistence.model.PermissionData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionMyBatisRepository implements PermissionRepository {

	private final PermissionDynamicMapper permissionDynamicMapper;

	public PermissionMyBatisRepository(PermissionDynamicMapper permissionDynamicMapper) {
		this.permissionDynamicMapper = permissionDynamicMapper;
	}

	@Override
	public int create(Permission permission) {
		return permissionDynamicMapper.create(toData(permission));
	}

	@Override
	public int update(Permission permission) {
		return permissionDynamicMapper.update(toData(permission));
	}

	@Override
	public int delete(Long id) {
		return permissionDynamicMapper.delete(id);
	}

	@Override
	public List<Permission> query(PermissionQueryOptions options) {
		return permissionDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private Permission toDomain(PermissionData data) {
		return new Permission(
				data.id(),
				data.code(),
				data.resource(),
				data.action(),
				data.name(),
				data.description(),
				data.enabled(),
				data.deleted(),
				data.createTime(),
				data.updateTime()
		);
	}

	private PermissionData toData(Permission permission) {
		return new PermissionData(
				permission.id(),
				permission.code(),
				permission.resource(),
				permission.action(),
				permission.name(),
				permission.description(),
				permission.enabled(),
				permission.deleted(),
				permission.createTime(),
				permission.updateTime()
		);
	}

}

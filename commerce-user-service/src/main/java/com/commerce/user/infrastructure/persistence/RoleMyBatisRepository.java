package com.commerce.user.infrastructure.persistence;

import com.commerce.user.domain.model.Role;
import com.commerce.user.domain.model.RoleQueryOptions;
import com.commerce.user.domain.port.RoleRepository;
import com.commerce.user.infrastructure.persistence.mapper.RoleDynamicMapper;
import com.commerce.user.infrastructure.persistence.model.RoleData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RoleMyBatisRepository implements RoleRepository {

	private final RoleDynamicMapper roleDynamicMapper;

	public RoleMyBatisRepository(RoleDynamicMapper roleDynamicMapper) {
		this.roleDynamicMapper = roleDynamicMapper;
	}

	@Override
	public int create(Role role) {
		return roleDynamicMapper.create(toData(role));
	}

	@Override
	public int update(Role role) {
		return roleDynamicMapper.update(toData(role));
	}

	@Override
	public int delete(Long id) {
		return roleDynamicMapper.delete(id);
	}

	@Override
	public List<Role> query(RoleQueryOptions options) {
		return roleDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private Role toDomain(RoleData data) {
		return new Role(
				data.id(),
				data.code(),
				data.name(),
				data.description(),
				data.enabled(),
				data.deleted(),
				data.createTime(),
				data.updateTime()
		);
	}

	private RoleData toData(Role role) {
		return new RoleData(
				role.id(),
				role.code(),
				role.name(),
				role.description(),
				role.enabled(),
				role.deleted(),
				role.createTime(),
				role.updateTime()
		);
	}

}

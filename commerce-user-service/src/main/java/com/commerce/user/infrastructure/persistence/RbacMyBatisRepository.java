package com.commerce.user.infrastructure.persistence;

import com.commerce.user.domain.model.AuthPrincipal;
import com.commerce.user.domain.port.RbacRepository;
import com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicMapper;
import com.commerce.user.infrastructure.persistence.mapper.RoleDynamicMapper;
import com.commerce.user.infrastructure.persistence.mapper.RolePermissionDynamicMapper;
import com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicMapper;
import com.commerce.user.infrastructure.persistence.mapper.UserRoleDynamicMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RbacMyBatisRepository implements RbacRepository {

	private final UserRoleDynamicMapper userRoleDynamicMapper;
	private final RoleDynamicMapper roleDynamicMapper;
	private final RolePermissionDynamicMapper rolePermissionDynamicMapper;
	private final PermissionDynamicMapper permissionDynamicMapper;
	private final UserCredentialDynamicMapper userCredentialDynamicMapper;

	public RbacMyBatisRepository(
			UserRoleDynamicMapper userRoleDynamicMapper,
			RoleDynamicMapper roleDynamicMapper,
			RolePermissionDynamicMapper rolePermissionDynamicMapper,
			PermissionDynamicMapper permissionDynamicMapper,
			UserCredentialDynamicMapper userCredentialDynamicMapper
	) {
		this.userRoleDynamicMapper = userRoleDynamicMapper;
		this.roleDynamicMapper = roleDynamicMapper;
		this.rolePermissionDynamicMapper = rolePermissionDynamicMapper;
		this.permissionDynamicMapper = permissionDynamicMapper;
		this.userCredentialDynamicMapper = userCredentialDynamicMapper;
	}

	@Override
	public List<String> rolesOf(Long userId) {
		List<Long> roleIds = userRoleDynamicMapper.selectByUserId(userId).stream()
				.map(userRole -> userRole.roleId())
				.distinct()
				.toList();
		return roleDynamicMapper.selectActiveByIds(roleIds).stream()
				.map(role -> role.code())
				.distinct()
				.toList();
	}

	@Override
	public List<String> permissionsOf(Long userId) {
		List<Long> roleIds = userRoleDynamicMapper.selectByUserId(userId).stream()
				.map(userRole -> userRole.roleId())
				.distinct()
				.toList();
		List<Long> permissionIds = rolePermissionDynamicMapper.selectByRoleIds(roleIds).stream()
				.map(rolePermission -> rolePermission.permissionId())
				.distinct()
				.toList();
		return permissionDynamicMapper.selectActiveByIds(permissionIds).stream()
				.map(permission -> permission.code())
				.distinct()
				.toList();
	}

	@Override
	public Optional<AuthPrincipal> principalOf(Long userId) {
		return userCredentialDynamicMapper.selectByUserId(userId)
				.map(credential -> new AuthPrincipal(
						credential.userId(),
						credential.username(),
						rolesOf(credential.userId()),
						permissionsOf(credential.userId())
				));
	}

}

package com.commerce.user.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.commerce.user.application.port.PermissionUseCase;
import com.commerce.user.application.port.RolePermissionUseCase;
import com.commerce.user.application.port.UserRoleUseCase;
import com.commerce.user.domain.model.Permission;
import com.commerce.user.domain.model.PermissionQueryOptions;
import com.commerce.user.domain.model.RolePermissionQueryOptions;
import com.commerce.user.domain.model.UserRoleQueryOptions;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rbac/permissions")
public class PermissionController {

	private final PermissionUseCase permissionUseCase;
	private final RolePermissionUseCase rolePermissionUseCase;
	private final UserRoleUseCase userRoleUseCase;

	public PermissionController(
			PermissionUseCase permissionUseCase,
			RolePermissionUseCase rolePermissionUseCase,
			UserRoleUseCase userRoleUseCase
	) {
		this.permissionUseCase = permissionUseCase;
		this.rolePermissionUseCase = rolePermissionUseCase;
		this.userRoleUseCase = userRoleUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public int create(@RequestBody Permission permission) {
		return permissionUseCase.create(permission);
	}

	@PutMapping("/{permissionId}")
	public int update(@PathVariable Long permissionId, @RequestBody Permission permission) {
		int rows = permissionUseCase.update(new Permission(
				permissionId,
				permission.code(),
				permission.resource(),
				permission.action(),
				permission.name(),
				permission.description(),
				permission.enabled(),
				permission.deleted(),
				permission.createTime(),
				permission.updateTime()
		));
		kickUsersWithPermission(permissionId);
		return rows;
	}

	@DeleteMapping("/{permissionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long permissionId) {
		permissionUseCase.delete(permissionId);
		kickUsersWithPermission(permissionId);
	}

	@GetMapping
	public List<Permission> query(
			@RequestParam Optional<Long> id,
			@RequestParam Optional<String> code,
			@RequestParam Optional<String> resource,
			@RequestParam Optional<String> action,
			@RequestParam Optional<Boolean> enabled,
			@RequestParam Optional<Boolean> deleted
	) {
		return permissionUseCase.query(new PermissionQueryOptions(id, code, resource, action, enabled, deleted));
	}

	private void kickUsersWithPermission(Long permissionId) {
		rolePermissionUseCase.query(new RolePermissionQueryOptions(Optional.empty(), Optional.of(permissionId))).stream()
				.map(assignment -> assignment.roleId())
				.distinct()
				.flatMap(roleId -> userRoleUseCase.query(new UserRoleQueryOptions(Optional.empty(), Optional.of(roleId))).stream())
				.map(assignment -> assignment.userId())
				.distinct()
				.forEach(StpUtil::logout);
	}

}

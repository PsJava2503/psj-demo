package com.commerce.user.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.commerce.user.application.port.RolePermissionUseCase;
import com.commerce.user.application.port.UserRoleUseCase;
import com.commerce.user.domain.model.RolePermissionAssignment;
import com.commerce.user.domain.model.RolePermissionQueryOptions;
import com.commerce.user.domain.model.UserRoleQueryOptions;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rbac/role-permissions")
public class RolePermissionController {

	private final RolePermissionUseCase rolePermissionUseCase;
	private final UserRoleUseCase userRoleUseCase;

	public RolePermissionController(RolePermissionUseCase rolePermissionUseCase, UserRoleUseCase userRoleUseCase) {
		this.rolePermissionUseCase = rolePermissionUseCase;
		this.userRoleUseCase = userRoleUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public int create(@RequestBody RolePermissionAssignment assignment) {
		int rows = rolePermissionUseCase.create(assignment);
		kickUsersWithRole(assignment.roleId());
		return rows;
	}

	@PutMapping
	public int update(@RequestBody RolePermissionAssignment assignment) {
		int rows = rolePermissionUseCase.update(assignment);
		kickUsersWithRole(assignment.roleId());
		return rows;
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@RequestParam Long roleId, @RequestParam Long permissionId) {
		rolePermissionUseCase.delete(new RolePermissionAssignment(roleId, permissionId, null, null));
		kickUsersWithRole(roleId);
	}

	@GetMapping
	public List<RolePermissionAssignment> query(
			@RequestParam Optional<Long> roleId,
			@RequestParam Optional<Long> permissionId
	) {
		return rolePermissionUseCase.query(new RolePermissionQueryOptions(roleId, permissionId));
	}

	private void kickUsersWithRole(Long roleId) {
		if (roleId == null) {
			return;
		}
		userRoleUseCase.query(new UserRoleQueryOptions(Optional.empty(), Optional.of(roleId))).stream()
				.map(assignment -> assignment.userId())
				.distinct()
				.forEach(StpUtil::logout);
	}

}

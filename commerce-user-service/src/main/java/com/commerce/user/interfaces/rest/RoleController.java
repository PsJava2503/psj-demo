package com.commerce.user.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.commerce.user.application.port.RoleUseCase;
import com.commerce.user.application.port.UserRoleUseCase;
import com.commerce.user.domain.model.Role;
import com.commerce.user.domain.model.RoleQueryOptions;
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
@RequestMapping("/api/rbac/roles")
public class RoleController {

	private final RoleUseCase roleUseCase;
	private final UserRoleUseCase userRoleUseCase;

	public RoleController(RoleUseCase roleUseCase, UserRoleUseCase userRoleUseCase) {
		this.roleUseCase = roleUseCase;
		this.userRoleUseCase = userRoleUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public int create(@RequestBody Role role) {
		return roleUseCase.create(role);
	}

	@PutMapping("/{roleId}")
	public int update(@PathVariable Long roleId, @RequestBody Role role) {
		int rows = roleUseCase.update(new Role(
				roleId,
				role.code(),
				role.name(),
				role.description(),
				role.enabled(),
				role.deleted(),
				role.createTime(),
				role.updateTime()
		));
		kickUsersWithRole(roleId);
		return rows;
	}

	@DeleteMapping("/{roleId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long roleId) {
		roleUseCase.delete(roleId);
		kickUsersWithRole(roleId);
	}

	@GetMapping
	public List<Role> query(
			@RequestParam Optional<Long> id,
			@RequestParam Optional<String> code,
			@RequestParam Optional<Boolean> enabled,
			@RequestParam Optional<Boolean> deleted
	) {
		return roleUseCase.query(new RoleQueryOptions(id, code, enabled, deleted));
	}

	private void kickUsersWithRole(Long roleId) {
		userRoleUseCase.query(new UserRoleQueryOptions(Optional.empty(), Optional.of(roleId))).stream()
				.map(assignment -> assignment.userId())
				.distinct()
				.forEach(StpUtil::logout);
	}

}

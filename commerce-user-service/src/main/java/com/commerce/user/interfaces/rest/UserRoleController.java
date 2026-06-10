package com.commerce.user.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import com.commerce.user.application.port.UserRoleUseCase;
import com.commerce.user.domain.model.UserRoleAssignment;
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
@RequestMapping("/api/rbac/user-roles")
public class UserRoleController {

	private final UserRoleUseCase userRoleUseCase;

	public UserRoleController(UserRoleUseCase userRoleUseCase) {
		this.userRoleUseCase = userRoleUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public int create(@RequestBody UserRoleAssignment assignment) {
		int rows = userRoleUseCase.create(assignment);
		kickUser(assignment.userId());
		return rows;
	}

	@PutMapping
	public int update(@RequestBody UserRoleAssignment assignment) {
		int rows = userRoleUseCase.update(assignment);
		kickUser(assignment.userId());
		return rows;
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@RequestParam Long userId, @RequestParam Long roleId) {
		userRoleUseCase.delete(new UserRoleAssignment(userId, roleId, null, null));
		kickUser(userId);
	}

	@GetMapping
	public List<UserRoleAssignment> query(@RequestParam Optional<Long> userId, @RequestParam Optional<Long> roleId) {
		return userRoleUseCase.query(new UserRoleQueryOptions(userId, roleId));
	}

	private void kickUser(Long userId) {
		if (userId != null) {
			StpUtil.logout(userId);
		}
	}

}

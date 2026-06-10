package com.commerce.user.infrastructure.security;

import cn.dev33.satoken.stp.StpInterface;
import com.commerce.user.domain.port.RbacRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StpInterfaceImpl implements StpInterface {

	private final RbacRepository rbacRepository;

	public StpInterfaceImpl(RbacRepository rbacRepository) {
		this.rbacRepository = rbacRepository;
	}

	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		return rbacRepository.permissionsOf(Long.valueOf(loginId.toString()));
	}

	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		return rbacRepository.rolesOf(Long.valueOf(loginId.toString()));
	}

}

package com.commerce.user.infrastructure.security;

import cn.dev33.satoken.stp.StpInterface;
import com.commerce.user.infrastructure.persistence.DemoRbacStore;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StpInterfaceImpl implements StpInterface {

	private final DemoRbacStore rbacStore;

	public StpInterfaceImpl(DemoRbacStore rbacStore) {
		this.rbacStore = rbacStore;
	}

	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		return rbacStore.getPermissions(Long.valueOf(loginId.toString()));
	}

	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		return rbacStore.getRoles(Long.valueOf(loginId.toString()));
	}

}

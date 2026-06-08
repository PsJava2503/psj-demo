package com.psj.commerce.order.infrastructure.rpc;

import com.psj.commerce.api.UserRpcService;
import com.psj.commerce.order.application.port.UserQueryPort;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class UserClient implements UserQueryPort {

	@DubboReference(check = false)
	private UserRpcService userRpcService;

	@Override
	public String getUserName(Long userId) {
		return userRpcService.getUserName(userId);
	}

}

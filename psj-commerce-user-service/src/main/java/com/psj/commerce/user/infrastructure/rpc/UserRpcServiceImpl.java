package com.psj.commerce.user.infrastructure.rpc;

import com.psj.commerce.api.UserRpcService;
import com.psj.commerce.user.application.port.UserUseCase;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class UserRpcServiceImpl implements UserRpcService {

	private final UserUseCase userUseCase;

	public UserRpcServiceImpl(UserUseCase userUseCase) {
		this.userUseCase = userUseCase;
	}

	@Override
	public String getUserName(Long userId) {
		return userUseCase.getUserName(userId);
	}

}

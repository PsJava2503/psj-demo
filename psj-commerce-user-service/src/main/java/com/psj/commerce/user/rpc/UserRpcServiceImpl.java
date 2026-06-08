package com.psj.commerce.user.rpc;

import com.psj.commerce.api.UserRpcService;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class UserRpcServiceImpl implements UserRpcService {

	@Override
	public String getUserName(Long userId) {
		return "user-" + userId;
	}

}

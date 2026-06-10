package com.commerce.order.infrastructure.rpc;

import com.commerce.order.application.port.UserQueryPort;
import com.commerce.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "commerce-user-service")
public interface UserClient extends UserQueryPort {

	@Override
	@GetMapping("/internal/users/{userId}")
	UserResponse getUser(@PathVariable("userId") Long userId);

}

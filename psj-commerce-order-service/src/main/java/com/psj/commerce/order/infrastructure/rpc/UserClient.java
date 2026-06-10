package com.psj.commerce.order.infrastructure.rpc;

import com.psj.commerce.order.application.port.UserQueryPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "psj-commerce-user-service")
public interface UserClient extends UserQueryPort {

	@Override
	@GetMapping("/internal/users/{userId}")
	String getUserName(@PathVariable("userId") Long userId);

}

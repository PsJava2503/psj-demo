package com.commerce.user.application.service;

import com.commerce.user.application.port.UserUseCase;
import com.commerce.user.domain.model.User;
import com.commerce.user.domain.model.UserQueryOptions;
import com.commerce.user.domain.service.UserDomainService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService implements UserUseCase {

	private final UserDomainService userDomainService;

	public UserApplicationService(UserDomainService userDomainService) {
		this.userDomainService = userDomainService;
	}

	@Override
	public List<User> query(UserQueryOptions options) {
		return userDomainService.query(options);
	}

}

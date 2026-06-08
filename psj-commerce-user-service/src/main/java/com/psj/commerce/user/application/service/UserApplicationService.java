package com.psj.commerce.user.application.service;

import com.psj.commerce.user.application.port.UserUseCase;
import com.psj.commerce.user.domain.service.UserDomainService;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService implements UserUseCase {

	private final UserDomainService userDomainService;

	public UserApplicationService(UserDomainService userDomainService) {
		this.userDomainService = userDomainService;
	}

	@Override
	public String getUserName(Long userId) {
		return userDomainService.userNameOf(userId);
	}

}

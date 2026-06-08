package com.psj.commerce.user.domain.service;

import org.springframework.stereotype.Service;

@Service
public class UserDomainService {

	public String userNameOf(Long userId) {
		if (userId == null) {
			return "unknown-user";
		}
		return "user-" + userId;
	}

}

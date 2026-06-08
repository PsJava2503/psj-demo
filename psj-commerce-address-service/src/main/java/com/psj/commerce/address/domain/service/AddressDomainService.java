package com.psj.commerce.address.domain.service;

import org.springframework.stereotype.Service;

@Service
public class AddressDomainService {

	public String defaultAddressOf(Long userId) {
		if (userId == null) {
			return "No default address";
		}
		return "Default Address of user-" + userId + ": Shanghai Pudong New Area 1001";
	}

}

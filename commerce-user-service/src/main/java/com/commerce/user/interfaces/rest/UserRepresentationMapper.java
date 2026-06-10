package com.commerce.user.interfaces.rest;

import com.commerce.user.UserResponse;
import com.commerce.user.domain.model.User;

final class UserRepresentationMapper {

	private UserRepresentationMapper() {
	}

	static UserResponse toResponse(User user) {
		return new UserResponse(
				user.id(),
				user.firstName(),
				user.secondName(),
				user.phone(),
				user.email(),
				user.defaultAddressId(),
				user.deleted(),
				user.createTime()
		);
	}

}

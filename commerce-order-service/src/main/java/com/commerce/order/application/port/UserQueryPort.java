package com.commerce.order.application.port;

import com.commerce.user.UserResponse;

public interface UserQueryPort {

	UserResponse getUser(Long userId);

}

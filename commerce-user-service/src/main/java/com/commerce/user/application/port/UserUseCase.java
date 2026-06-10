package com.commerce.user.application.port;

import com.commerce.user.domain.model.User;
import com.commerce.user.domain.model.UserQueryOptions;
import java.util.List;

public interface UserUseCase {

	List<User> query(UserQueryOptions options);

}

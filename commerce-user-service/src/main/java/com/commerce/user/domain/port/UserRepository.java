package com.commerce.user.domain.port;

import com.commerce.user.domain.model.User;
import com.commerce.user.domain.model.UserQueryOptions;
import java.util.List;

public interface UserRepository {

	int create(User user);

	int update(User user);

	int delete(Long id);

	List<User> query(UserQueryOptions options);

}

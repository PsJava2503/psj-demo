package com.commerce.user.domain.service;

import com.commerce.user.domain.model.User;
import com.commerce.user.domain.model.UserQueryOptions;
import com.commerce.user.domain.port.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserDomainService {

	private final UserRepository userRepository;

	public UserDomainService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public int create(User user) {
		return userRepository.create(user);
	}

	public int update(User user) {
		return userRepository.update(user);
	}

	public int delete(Long id) {
		return userRepository.delete(id);
	}

	public List<User> query(UserQueryOptions options) {
		return userRepository.query(options);
	}

}

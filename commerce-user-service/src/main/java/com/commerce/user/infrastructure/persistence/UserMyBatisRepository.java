package com.commerce.user.infrastructure.persistence;

import com.commerce.user.domain.model.User;
import com.commerce.user.domain.model.UserQueryOptions;
import com.commerce.user.domain.port.UserRepository;
import com.commerce.user.infrastructure.persistence.mapper.UserDynamicMapper;
import com.commerce.user.infrastructure.persistence.model.UserData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserMyBatisRepository implements UserRepository {

	private final UserDynamicMapper userDynamicMapper;

	public UserMyBatisRepository(UserDynamicMapper userDynamicMapper) {
		this.userDynamicMapper = userDynamicMapper;
	}

	@Override
	public int create(User user) {
		return userDynamicMapper.create(toData(user));
	}

	@Override
	public int update(User user) {
		return userDynamicMapper.update(toData(user));
	}

	@Override
	public int delete(Long id) {
		return userDynamicMapper.delete(id);
	}

	@Override
	public List<User> query(UserQueryOptions options) {
		UserQueryOptions safeOptions = options == null ? UserQueryOptions.none() : options;
		return userDynamicMapper.query(safeOptions).stream()
				.map(this::toDomain)
				.toList();
	}

	private User toDomain(UserData data) {
		return new User(
				data.id(),
				data.firstName(),
				data.secondName(),
				data.phone(),
				data.email(),
				data.defaultAddressId(),
				data.deleted(),
				data.createTime()
		);
	}

	private UserData toData(User user) {
		return new UserData(
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

package com.commerce.user.infrastructure.persistence;

import com.commerce.user.domain.model.UserCredential;
import com.commerce.user.domain.model.UserCredentialQueryOptions;
import com.commerce.user.domain.model.UserQueryOptions;
import com.commerce.user.domain.port.CredentialRepository;
import com.commerce.user.infrastructure.persistence.mapper.UserDynamicMapper;
import com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicMapper;
import com.commerce.user.infrastructure.persistence.model.UserCredentialData;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserCredentialMyBatisRepository implements CredentialRepository {

	private final UserCredentialDynamicMapper userCredentialDynamicMapper;
	private final UserDynamicMapper userDynamicMapper;

	public UserCredentialMyBatisRepository(
			UserCredentialDynamicMapper userCredentialDynamicMapper,
			UserDynamicMapper userDynamicMapper
	) {
		this.userCredentialDynamicMapper = userCredentialDynamicMapper;
		this.userDynamicMapper = userDynamicMapper;
	}

	@Override
	public int create(UserCredential credential) {
		return userCredentialDynamicMapper.create(toData(credential));
	}

	@Override
	public int update(UserCredential credential) {
		return userCredentialDynamicMapper.update(toData(credential));
	}

	@Override
	public int delete(Long userId) {
		return userCredentialDynamicMapper.delete(userId);
	}

	@Override
	public List<UserCredential> query(UserCredentialQueryOptions options) {
		return userCredentialDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	@Override
	public Optional<UserCredential> findByLogin(String login) {
		if (login == null || login.isBlank()) {
			return Optional.empty();
		}
		String normalizedLogin = login.trim();
		return userCredentialDynamicMapper.selectByUsername(normalizedLogin)
				.or(() -> selectByEmail(normalizedLogin))
				.or(() -> selectByPhone(normalizedLogin))
				.map(this::toDomain);
	}

	private Optional<UserCredentialData> selectByEmail(String email) {
		return userDynamicMapper.query(new UserQueryOptions(
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.of(email),
						Optional.empty(),
						Optional.of(true),
						Optional.of(false),
						Optional.empty(),
						Optional.empty()
				)).stream()
				.findFirst()
				.flatMap(user -> userCredentialDynamicMapper.selectByUserId(user.id()));
	}

	private Optional<UserCredentialData> selectByPhone(String phone) {
		return userDynamicMapper.query(new UserQueryOptions(
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.of(phone),
						Optional.empty(),
						Optional.empty(),
						Optional.of(true),
						Optional.of(false),
						Optional.empty(),
						Optional.empty()
				)).stream()
				.findFirst()
				.flatMap(user -> userCredentialDynamicMapper.selectByUserId(user.id()));
	}

	private UserCredential toDomain(UserCredentialData data) {
		return new UserCredential(
				data.userId(),
				data.username(),
				data.passwordHash(),
				data.passwordSalt(),
				data.passwordAlgorithm(),
				data.enabled(),
				data.createTime(),
				data.updateTime()
		);
	}

	private UserCredentialData toData(UserCredential credential) {
		return new UserCredentialData(
				credential.userId(),
				credential.username(),
				credential.passwordHash(),
				credential.passwordSalt(),
				credential.passwordAlgorithm(),
				credential.enabled(),
				credential.createTime(),
				credential.updateTime()
		);
	}

}

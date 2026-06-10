package com.commerce.user.infrastructure.persistence;

import com.commerce.user.domain.model.User;
import com.commerce.user.domain.model.UserAddressSlot;
import com.commerce.user.domain.model.UserQueryOptions;
import com.commerce.user.domain.port.RbacRepository;
import com.commerce.user.domain.port.UserRepository;
import com.commerce.user.infrastructure.persistence.mapper.UserAddressSlotDynamicMapper;
import com.commerce.user.infrastructure.persistence.mapper.UserDynamicMapper;
import com.commerce.user.infrastructure.persistence.model.UserAddressSlotData;
import com.commerce.user.infrastructure.persistence.model.UserData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserMyBatisRepository implements UserRepository {

	private final UserDynamicMapper userDynamicMapper;
	private final RbacRepository rbacRepository;
	private final UserAddressSlotDynamicMapper userAddressSlotDynamicMapper;

	public UserMyBatisRepository(
			UserDynamicMapper userDynamicMapper,
			RbacRepository rbacRepository,
			UserAddressSlotDynamicMapper userAddressSlotDynamicMapper
	) {
		this.userDynamicMapper = userDynamicMapper;
		this.rbacRepository = rbacRepository;
		this.userAddressSlotDynamicMapper = userAddressSlotDynamicMapper;
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
				data.defaultAddressSlotId(),
				rolesOf(data.id()),
				addressSlotsOf(data.id()),
				data.enabled(),
				data.deleted(),
				data.createTime(),
				data.updateTime()
		);
	}

	private UserData toData(User user) {
		return new UserData(
				user.id(),
				user.firstName(),
				user.secondName(),
				user.phone(),
				user.email(),
				user.defaultAddressSlotId(),
				user.enabled(),
				user.deleted(),
				user.createTime(),
				user.updateTime()
		);
	}

	private List<String> rolesOf(Long userId) {
		return rbacRepository.rolesOf(userId);
	}

	private List<UserAddressSlot> addressSlotsOf(Long userId) {
		return userAddressSlotDynamicMapper.selectByUserId(userId).stream()
				.map(this::toDomain)
				.toList();
	}

	private UserAddressSlot toDomain(UserAddressSlotData data) {
		return new UserAddressSlot(
				data.id(),
				data.userId(),
				data.addressId(),
				data.slotName(),
				data.deleted(),
				data.createTime()
		);
	}

}

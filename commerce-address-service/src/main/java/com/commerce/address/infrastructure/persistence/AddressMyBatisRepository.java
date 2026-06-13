package com.commerce.address.infrastructure.persistence;

import com.commerce.address.domain.model.Address;
import com.commerce.address.domain.model.AddressQueryOptions;
import com.commerce.address.domain.port.AddressRepository;
import com.commerce.address.infrastructure.persistence.mapper.AddressDynamicMapper;
import com.commerce.address.infrastructure.persistence.model.AddressData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AddressMyBatisRepository implements AddressRepository {

	private final AddressDynamicMapper addressDynamicMapper;

	public AddressMyBatisRepository(AddressDynamicMapper addressDynamicMapper) {
		this.addressDynamicMapper = addressDynamicMapper;
	}

	@Override
	public int create(Address address) {
		return addressDynamicMapper.create(toData(address));
	}

	@Override
	public int update(Address address, AddressQueryOptions options) {
		return addressDynamicMapper.update(toData(address), options);
	}

	@Override
	public int delete(AddressQueryOptions options) {
		return addressDynamicMapper.delete(options);
	}

	@Override
	public List<Address> query(AddressQueryOptions options) {
		return addressDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private Address toDomain(AddressData data) {
		return new Address(
				data.id(),
				data.userId(),
				data.recipientName(),
				data.phone(),
				data.province(),
				data.city(),
				data.district(),
				data.detail(),
				data.defaultAddress(),
				data.deleted(),
				data.createTime(),
				data.updateTime()
		);
	}

	private AddressData toData(Address address) {
		return new AddressData(
				address.id(),
				address.userId(),
				address.recipientName(),
				address.phone(),
				address.province(),
				address.city(),
				address.district(),
				address.detail(),
				address.defaultAddress(),
				address.deleted(),
				address.createTime(),
				address.updateTime()
		);
	}
}

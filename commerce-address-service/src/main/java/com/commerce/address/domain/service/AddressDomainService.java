package com.commerce.address.domain.service;

import com.commerce.address.domain.model.Address;
import com.commerce.address.domain.model.AddressQueryOptions;
import com.commerce.address.domain.port.AddressRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AddressDomainService {

	private final AddressRepository addressRepository;

	public AddressDomainService(AddressRepository addressRepository) {
		this.addressRepository = addressRepository;
	}

	public int create(Address address) {
		if (Boolean.TRUE.equals(address.defaultAddress())) {
			clearDefaultAddress(address.userId());
		}
		return addressRepository.create(address);
	}

	public int update(Address address, AddressQueryOptions options) {
		if (Boolean.TRUE.equals(address.defaultAddress())) {
			clearDefaultAddress(address.userId());
		}
		return addressRepository.update(address, options);
	}

	public int delete(AddressQueryOptions options) {
		return addressRepository.delete(options);
	}

	public List<Address> query(AddressQueryOptions options) {
		return addressRepository.query(options);
	}

	public Optional<Address> defaultAddressOf(Long userId) {
		if (userId == null) {
			return Optional.empty();
		}
		return addressRepository.query(new AddressQueryOptions(
				Optional.empty(),
				Optional.of(userId),
				Optional.empty(),
				Optional.of(true),
				Optional.of(false)
		)).stream().findFirst();
	}

	private void clearDefaultAddress(Long userId) {
		if (userId == null) {
			return;
		}
		addressRepository.update(new Address(
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				null,
				null,
				null
		), new AddressQueryOptions(
				Optional.empty(),
				Optional.of(userId),
				Optional.empty(),
				Optional.of(true),
				Optional.of(false)
		));
	}

}

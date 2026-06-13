package com.commerce.address.application.service;

import com.commerce.address.application.port.AddressUseCase;
import com.commerce.address.domain.model.Address;
import com.commerce.address.domain.model.AddressQueryOptions;
import com.commerce.address.domain.service.AddressDomainService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AddressApplicationService implements AddressUseCase {

	private final AddressDomainService addressDomainService;

	public AddressApplicationService(AddressDomainService addressDomainService) {
		this.addressDomainService = addressDomainService;
	}

	@Override
	public int create(Address address) {
		return addressDomainService.create(address);
	}

	@Override
	public int update(Address address, AddressQueryOptions options) {
		return addressDomainService.update(address, options);
	}

	@Override
	public int delete(AddressQueryOptions options) {
		return addressDomainService.delete(options);
	}

	@Override
	public List<Address> query(AddressQueryOptions options) {
		return addressDomainService.query(options);
	}

	@Override
	public Optional<Address> getDefaultAddress(Long userId) {
		return addressDomainService.defaultAddressOf(userId);
	}

}

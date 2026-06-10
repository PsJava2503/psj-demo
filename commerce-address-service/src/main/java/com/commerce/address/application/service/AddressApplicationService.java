package com.commerce.address.application.service;

import com.commerce.address.application.port.AddressUseCase;
import com.commerce.address.domain.service.AddressDomainService;
import org.springframework.stereotype.Service;

@Service
public class AddressApplicationService implements AddressUseCase {

	private final AddressDomainService addressDomainService;

	public AddressApplicationService(AddressDomainService addressDomainService) {
		this.addressDomainService = addressDomainService;
	}

	@Override
	public String getDefaultAddress(Long userId) {
		return addressDomainService.defaultAddressOf(userId);
	}

}

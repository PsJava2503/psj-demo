package com.commerce.address.interfaces.rest;

import com.commerce.address.AddressResponse;
import com.commerce.address.application.port.AddressUseCase;
import com.commerce.address.domain.model.Address;
import com.commerce.address.domain.model.AddressQueryOptions;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/addresses")
public class InternalAddressController {

	private final AddressUseCase addressUseCase;

	public InternalAddressController(AddressUseCase addressUseCase) {
		this.addressUseCase = addressUseCase;
	}

	@GetMapping("/{addressId}")
	public AddressResponse get(@PathVariable Long addressId) {
		return addressUseCase.query(new AddressQueryOptions(
				Optional.of(addressId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(false)
		)).stream()
				.findFirst()
				.map(this::toResponse)
				.orElseThrow(() -> new IllegalArgumentException("address not found: " + addressId));
	}

	private AddressResponse toResponse(Address address) {
		return new AddressResponse(
				address.id(),
				address.userId(),
				address.recipientName(),
				address.phone(),
				address.province(),
				address.city(),
				address.district(),
				address.detail(),
				address.defaultAddress()
		);
	}
}

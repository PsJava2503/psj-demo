package com.psj.commerce.address.interfaces.rest;

import com.psj.commerce.address.application.port.AddressUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

	private final AddressUseCase addressUseCase;

	public AddressController(AddressUseCase addressUseCase) {
		this.addressUseCase = addressUseCase;
	}

	@GetMapping("/{userId}/default")
	public String defaultAddress(@PathVariable Long userId) {
		return addressUseCase.getDefaultAddress(userId);
	}

}

package com.commerce.address.interfaces.rest;

import com.commerce.address.AddressResponse;
import com.commerce.address.application.port.AddressUseCase;
import com.commerce.address.domain.model.Address;
import com.commerce.address.domain.model.AddressQueryOptions;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

	private final AddressUseCase addressUseCase;

	public AddressController(AddressUseCase addressUseCase) {
		this.addressUseCase = addressUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public int create(@RequestBody Address address) {
		return addressUseCase.create(address);
	}

	@PutMapping("/{addressId}")
	public int update(@PathVariable Long addressId, @RequestBody Address address) {
		return addressUseCase.update(new Address(
				addressId,
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
		), new AddressQueryOptions(
				Optional.of(addressId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		));
	}

	@DeleteMapping("/{addressId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long addressId) {
		addressUseCase.delete(new AddressQueryOptions(
				Optional.of(addressId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		));
	}

	@GetMapping
	public List<AddressResponse> query(
			@RequestParam Optional<Long> id,
			@RequestParam Optional<Long> userId,
			@RequestParam Optional<String> phone,
			@RequestParam Optional<Boolean> defaultAddress,
			@RequestParam Optional<Boolean> deleted
	) {
		return addressUseCase.query(new AddressQueryOptions(id, userId, phone, defaultAddress, deleted)).stream()
				.map(this::toResponse)
				.toList();
	}

	@GetMapping("/{addressId}")
	public Optional<AddressResponse> get(@PathVariable Long addressId) {
		return addressUseCase.query(new AddressQueryOptions(
				Optional.of(addressId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(false)
		)).stream()
				.findFirst()
				.map(this::toResponse);
	}

	@GetMapping("/{userId}/default")
	public Optional<AddressResponse> defaultAddress(@PathVariable Long userId) {
		return addressUseCase.getDefaultAddress(userId).map(this::toResponse);
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

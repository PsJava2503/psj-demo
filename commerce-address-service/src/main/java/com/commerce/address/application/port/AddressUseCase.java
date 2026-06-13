package com.commerce.address.application.port;

import com.commerce.address.domain.model.Address;
import com.commerce.address.domain.model.AddressQueryOptions;
import java.util.List;
import java.util.Optional;

public interface AddressUseCase {

	int create(Address address);

	int update(Address address, AddressQueryOptions options);

	int delete(AddressQueryOptions options);

	List<Address> query(AddressQueryOptions options);

	Optional<Address> getDefaultAddress(Long userId);

}

package com.commerce.address.domain.port;

import com.commerce.address.domain.model.Address;
import com.commerce.address.domain.model.AddressQueryOptions;
import java.util.List;

public interface AddressRepository {

	int create(Address address);

	int update(Address address, AddressQueryOptions options);

	int delete(AddressQueryOptions options);

	List<Address> query(AddressQueryOptions options);
}

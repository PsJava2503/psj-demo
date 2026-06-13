package com.commerce.order.application.port;

import com.commerce.address.AddressResponse;

public interface AddressQueryPort {

	AddressResponse getAddress(Long addressId);
}

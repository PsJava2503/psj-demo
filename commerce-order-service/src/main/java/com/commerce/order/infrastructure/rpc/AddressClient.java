package com.commerce.order.infrastructure.rpc;

import com.commerce.address.AddressResponse;
import com.commerce.order.application.port.AddressQueryPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "commerce-address-service")
public interface AddressClient extends AddressQueryPort {

	@Override
	@GetMapping("/internal/addresses/{addressId}")
	AddressResponse getAddress(@PathVariable("addressId") Long addressId);
}

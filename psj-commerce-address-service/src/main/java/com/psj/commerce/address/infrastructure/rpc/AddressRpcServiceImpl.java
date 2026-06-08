package com.psj.commerce.address.infrastructure.rpc;

import com.psj.commerce.address.application.port.AddressUseCase;
import com.psj.commerce.api.AddressRpcService;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class AddressRpcServiceImpl implements AddressRpcService {

	private final AddressUseCase addressUseCase;

	public AddressRpcServiceImpl(AddressUseCase addressUseCase) {
		this.addressUseCase = addressUseCase;
	}

	@Override
	public String getDefaultAddress(Long userId) {
		return addressUseCase.getDefaultAddress(userId);
	}

}

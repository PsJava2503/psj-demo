package com.psj.commerce.inventory.infrastructure.rpc;

import com.psj.commerce.api.InventoryRpcService;
import com.psj.commerce.inventory.application.port.InventoryUseCase;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class InventoryRpcServiceImpl implements InventoryRpcService {

	private final InventoryUseCase inventoryUseCase;

	public InventoryRpcServiceImpl(InventoryUseCase inventoryUseCase) {
		this.inventoryUseCase = inventoryUseCase;
	}

	@Override
	public boolean deductStock(Long productId, Integer quantity) {
		return inventoryUseCase.deductStock(productId, quantity);
	}

}

package com.psj.commerce.order.infrastructure.rpc;

import com.psj.commerce.api.InventoryRpcService;
import com.psj.commerce.order.application.port.InventoryCommandPort;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class InventoryClient implements InventoryCommandPort {

	@DubboReference(check = false)
	private InventoryRpcService inventoryRpcService;

	@Override
	public boolean deductStock(Long productId, Integer quantity) {
		return inventoryRpcService.deductStock(productId, quantity);
	}

}

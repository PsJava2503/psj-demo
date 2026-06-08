package com.psj.commerce.inventory.rpc;

import com.psj.commerce.api.InventoryRpcService;
import org.apache.dubbo.config.annotation.DubboService;

@org.springframework.stereotype.Service
@DubboService
public class InventoryRpcServiceImpl implements InventoryRpcService {

	@Override
	public boolean deductStock(Long productId, Integer quantity) {
		return productId != null && quantity != null && quantity > 0;
	}

}

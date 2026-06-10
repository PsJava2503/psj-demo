package com.commerce.inventory.domain.service;

import org.springframework.stereotype.Service;

@Service
public class InventoryDomainService {

	public boolean canDeduct(Long productId, Integer quantity) {
		return productId != null && quantity != null && quantity > 0;
	}

}

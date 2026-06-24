package com.commerce.cart.application.service;

import com.commerce.cart.application.port.CartUseCase;
import com.commerce.cart.domain.model.CartItem;
import com.commerce.cart.domain.model.CartItemQueryOptions;
import com.commerce.cart.domain.service.CartDomainService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CartApplicationService implements CartUseCase {

	private final CartDomainService cartDomainService;

	public CartApplicationService(CartDomainService cartDomainService) {
		this.cartDomainService = cartDomainService;
	}

	@Override
	public int add(CartItem cartItem) {
		return cartDomainService.add(cartItem);
	}

	@Override
	public int updateQuantity(Long itemId, Long userId, Integer quantity) {
		return cartDomainService.updateQuantity(itemId, userId, quantity);
	}

	@Override
	public int deleteItem(Long itemId, Long userId) {
		return cartDomainService.deleteItem(itemId, userId);
	}

	@Override
	public int clear(Long userId) {
		return cartDomainService.clear(userId);
	}

	@Override
	public List<CartItem> query(CartItemQueryOptions options) {
		return cartDomainService.query(options);
	}
}

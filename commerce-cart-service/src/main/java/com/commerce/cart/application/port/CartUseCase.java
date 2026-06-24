package com.commerce.cart.application.port;

import com.commerce.cart.domain.model.CartItem;
import com.commerce.cart.domain.model.CartItemQueryOptions;
import java.util.List;

public interface CartUseCase {

	int add(CartItem cartItem);

	int updateQuantity(Long itemId, Long userId, Integer quantity);

	int deleteItem(Long itemId, Long userId);

	int clear(Long userId);

	List<CartItem> query(CartItemQueryOptions options);
}

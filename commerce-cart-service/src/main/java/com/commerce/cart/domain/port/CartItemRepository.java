package com.commerce.cart.domain.port;

import com.commerce.cart.domain.model.CartItem;
import com.commerce.cart.domain.model.CartItemQueryOptions;
import java.util.List;

public interface CartItemRepository {

	int create(CartItem cartItem);

	int update(CartItem cartItem, CartItemQueryOptions options);

	int delete(CartItemQueryOptions options);

	List<CartItem> query(CartItemQueryOptions options);
}

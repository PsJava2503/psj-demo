package com.commerce.cart.domain.service;

import com.commerce.cart.domain.model.CartItem;
import com.commerce.cart.domain.model.CartItemQueryOptions;
import com.commerce.cart.domain.port.CartItemRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CartDomainService {

	private final CartItemRepository cartItemRepository;

	public CartDomainService(CartItemRepository cartItemRepository) {
		this.cartItemRepository = cartItemRepository;
	}

	public int add(CartItem cartItem) {
		validateAdd(cartItem);
		Optional<CartItem> existingItem = cartItemRepository.query(new CartItemQueryOptions(
				Optional.empty(),
				Optional.of(cartItem.userId()),
				Optional.of(cartItem.productId()),
				Optional.empty(),
				Optional.of(false)
		)).stream().findFirst();

		if (existingItem.isPresent()) {
			CartItem existing = existingItem.get();
			return cartItemRepository.update(new CartItem(
					null,
					null,
					null,
					cartItem.skuId(),
					cartItem.productName(),
					cartItem.unitPrice(),
					existing.quantity() + cartItem.quantity(),
					true,
					null,
					null,
					null
			), new CartItemQueryOptions(
					Optional.of(existing.id()),
					Optional.of(existing.userId()),
					Optional.empty(),
					Optional.empty(),
					Optional.of(false)
			));
		}

		return cartItemRepository.create(new CartItem(
				null,
				cartItem.userId(),
				cartItem.productId(),
				cartItem.skuId(),
				cartItem.productName(),
				cartItem.unitPrice(),
				cartItem.quantity(),
				cartItem.selected() == null ? true : cartItem.selected(),
				false,
				null,
				null
		));
	}

	public int updateQuantity(Long itemId, Long userId, Integer quantity) {
		if (quantity == null || quantity <= 0) {
			return deleteItem(itemId, userId);
		}
		return cartItemRepository.update(new CartItem(
				null,
				null,
				null,
				null,
				null,
				null,
				quantity,
				null,
				null,
				null,
				null
		), new CartItemQueryOptions(
				Optional.ofNullable(itemId),
				Optional.ofNullable(userId),
				Optional.empty(),
				Optional.empty(),
				Optional.of(false)
		));
	}

	public int deleteItem(Long itemId, Long userId) {
		return cartItemRepository.delete(new CartItemQueryOptions(
				Optional.ofNullable(itemId),
				Optional.ofNullable(userId),
				Optional.empty(),
				Optional.empty(),
				Optional.of(false)
		));
	}

	public int clear(Long userId) {
		return cartItemRepository.delete(new CartItemQueryOptions(
				Optional.empty(),
				Optional.ofNullable(userId),
				Optional.empty(),
				Optional.empty(),
				Optional.of(false)
		));
	}

	public List<CartItem> query(CartItemQueryOptions options) {
		return cartItemRepository.query(options);
	}

	private void validateAdd(CartItem cartItem) {
		if (cartItem.userId() == null) {
			throw new IllegalArgumentException("userId is required");
		}
		if (cartItem.productId() == null) {
			throw new IllegalArgumentException("productId is required");
		}
		if (cartItem.skuId() == null) {
			throw new IllegalArgumentException("skuId is required");
		}
		if (cartItem.unitPrice() == null) {
			throw new IllegalArgumentException("unitPrice is required");
		}
		if (cartItem.quantity() == null || cartItem.quantity() <= 0) {
			throw new IllegalArgumentException("quantity must be positive");
		}
		if (cartItem.productName() == null || cartItem.productName().isBlank()) {
			throw new IllegalArgumentException("productName is required");
		}
	}
}

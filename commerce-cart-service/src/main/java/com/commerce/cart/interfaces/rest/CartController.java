package com.commerce.cart.interfaces.rest;

import com.commerce.cart.CartItemResponse;
import com.commerce.cart.application.port.CartUseCase;
import com.commerce.cart.domain.model.CartItem;
import com.commerce.cart.domain.model.CartItemQueryOptions;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
public class CartController {

	private final CartUseCase cartUseCase;

	public CartController(CartUseCase cartUseCase) {
		this.cartUseCase = cartUseCase;
	}

	@PostMapping("/items")
	@ResponseStatus(HttpStatus.CREATED)
	public int add(@RequestBody AddCartItemRequest request) {
		return cartUseCase.add(new CartItem(
				null,
				request.userId(),
				request.productId(),
				request.skuId(),
				request.productName(),
				request.unitPrice(),
				request.quantity(),
				true,
				false,
				null,
				null
		));
	}

	@PutMapping("/items/{itemId}")
	public int updateQuantity(@PathVariable Long itemId, @RequestBody UpdateCartItemRequest request) {
		return cartUseCase.updateQuantity(itemId, request.userId(), request.quantity());
	}

	@DeleteMapping("/items/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteItem(@PathVariable Long itemId, @RequestParam Long userId) {
		cartUseCase.deleteItem(itemId, userId);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void clear(@RequestParam Long userId) {
		cartUseCase.clear(userId);
	}

	@GetMapping
	public List<CartItemResponse> query(
			@RequestParam Optional<Long> id,
			@RequestParam Optional<Long> userId,
			@RequestParam Optional<Long> productId,
			@RequestParam Optional<Boolean> selected,
			@RequestParam Optional<Boolean> deleted
	) {
		return cartUseCase.query(new CartItemQueryOptions(id, userId, productId, selected, deleted)).stream()
				.map(this::toResponse)
				.toList();
	}

	@GetMapping("/items/{itemId}")
	public Optional<CartItemResponse> get(@PathVariable Long itemId) {
		return cartUseCase.query(new CartItemQueryOptions(
				Optional.of(itemId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(false)
		)).stream()
				.findFirst()
				.map(this::toResponse);
	}

	private CartItemResponse toResponse(CartItem cartItem) {
		return new CartItemResponse(
				cartItem.id(),
				cartItem.userId(),
				cartItem.productId(),
				cartItem.skuId(),
				cartItem.productName(),
				cartItem.unitPrice(),
				cartItem.quantity(),
				cartItem.selected(),
				cartItem.createTime(),
				cartItem.updateTime()
		);
	}
}

package com.commerce.cart.infrastructure.persistence;

import com.commerce.cart.domain.model.CartItem;
import com.commerce.cart.domain.model.CartItemQueryOptions;
import com.commerce.cart.domain.port.CartItemRepository;
import com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicMapper;
import com.commerce.cart.infrastructure.persistence.model.CartItemData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CartItemMyBatisRepository implements CartItemRepository {

	private final CartItemDynamicMapper cartItemDynamicMapper;

	public CartItemMyBatisRepository(CartItemDynamicMapper cartItemDynamicMapper) {
		this.cartItemDynamicMapper = cartItemDynamicMapper;
	}

	@Override
	public int create(CartItem cartItem) {
		return cartItemDynamicMapper.create(toData(cartItem));
	}

	@Override
	public int update(CartItem cartItem, CartItemQueryOptions options) {
		return cartItemDynamicMapper.update(toData(cartItem), options);
	}

	@Override
	public int delete(CartItemQueryOptions options) {
		return cartItemDynamicMapper.delete(options);
	}

	@Override
	public List<CartItem> query(CartItemQueryOptions options) {
		return cartItemDynamicMapper.query(options).stream()
				.map(this::toDomain)
				.toList();
	}

	private CartItem toDomain(CartItemData data) {
		return new CartItem(
				data.id(),
				data.userId(),
				data.productId(),
				data.skuId(),
				data.productName(),
				data.unitPrice(),
				data.quantity(),
				data.selected(),
				data.deleted(),
				data.createTime(),
				data.updateTime()
		);
	}

	private CartItemData toData(CartItem cartItem) {
		return new CartItemData(
				cartItem.id(),
				cartItem.userId(),
				cartItem.productId(),
				cartItem.skuId(),
				cartItem.productName(),
				cartItem.unitPrice(),
				cartItem.quantity(),
				cartItem.selected(),
				cartItem.deleted(),
				cartItem.createTime(),
				cartItem.updateTime()
		);
	}
}

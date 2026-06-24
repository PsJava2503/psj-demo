package com.commerce.cart.interfaces.rest;

public record UpdateCartItemRequest(Long userId, Integer quantity) {
}

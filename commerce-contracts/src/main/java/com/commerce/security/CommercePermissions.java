package com.commerce.security;

public final class CommercePermissions {

	public static final String ORDER_CREATE = "order:create";
	public static final String ORDER_VIEW = "order:view";
	public static final String PAYMENT_PAY = "payment:pay";
	public static final String INVENTORY_VIEW = "inventory:view";
	public static final String INVENTORY_UPDATE = "inventory:update";
	public static final String PRODUCT_VIEW = "product:view";
	public static final String PRODUCT_CREATE = "product:create";
	public static final String PRODUCT_UPDATE = "product:update";
	public static final String USER_VIEW = "user:view";
	public static final String NOTIFICATION_SEND = "notification:send";
	public static final String ADDRESS_VIEW = "address:view";

	private CommercePermissions() {
	}

}

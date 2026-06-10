package com.commerce.user.infrastructure.persistence;

import com.commerce.security.CommercePermissions;
import com.commerce.security.CommerceRoles;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DemoRbacStore {

	private final Map<String, DemoUser> usersByUsername = Map.of(
			"admin", new DemoUser(1L, "admin", "123456", List.of(CommerceRoles.ADMIN)),
			"customer", new DemoUser(2L, "customer", "123456", List.of(CommerceRoles.CUSTOMER)),
			"merchant", new DemoUser(3L, "merchant", "123456", List.of(CommerceRoles.MERCHANT))
	);

	private final Map<String, List<String>> permissionsByRole = Map.of(
			CommerceRoles.ADMIN, List.of(
					CommercePermissions.ORDER_CREATE,
					CommercePermissions.ORDER_VIEW,
					CommercePermissions.PAYMENT_PAY,
					CommercePermissions.INVENTORY_VIEW,
					CommercePermissions.INVENTORY_UPDATE,
					CommercePermissions.PRODUCT_VIEW,
					CommercePermissions.PRODUCT_CREATE,
					CommercePermissions.PRODUCT_UPDATE,
					CommercePermissions.USER_VIEW,
					CommercePermissions.NOTIFICATION_SEND,
					CommercePermissions.ADDRESS_VIEW
			),
			CommerceRoles.CUSTOMER, List.of(
					CommercePermissions.ORDER_CREATE,
					CommercePermissions.ORDER_VIEW,
					CommercePermissions.PAYMENT_PAY,
					CommercePermissions.PRODUCT_VIEW,
					CommercePermissions.ADDRESS_VIEW
			),
			CommerceRoles.MERCHANT, List.of(
					CommercePermissions.ORDER_VIEW,
					CommercePermissions.INVENTORY_VIEW,
					CommercePermissions.INVENTORY_UPDATE,
					CommercePermissions.PRODUCT_VIEW,
					CommercePermissions.PRODUCT_CREATE,
					CommercePermissions.PRODUCT_UPDATE,
					CommercePermissions.ADDRESS_VIEW
			)
	);

	public DemoUser authenticate(String username, String password) {
		DemoUser user = usersByUsername.get(username);
		if (user == null || !user.password().equals(password)) {
			return null;
		}
		return user;
	}

	public DemoUser getById(Long userId) {
		return usersByUsername.values().stream()
				.filter(user -> user.userId().equals(userId))
				.findFirst()
				.orElse(null);
	}

	public List<String> getRoles(Long userId) {
		DemoUser user = getById(userId);
		return user == null ? List.of() : user.roles();
	}

	public List<String> getPermissions(Long userId) {
		return getRoles(userId).stream()
				.flatMap(role -> permissionsByRole.getOrDefault(role, List.of()).stream())
				.distinct()
				.toList();
	}

	public record DemoUser(Long userId, String username, String password, List<String> roles) {
	}

}

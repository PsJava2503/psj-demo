package com.commerce.user.domain.port;

import com.commerce.user.domain.model.AuthPrincipal;
import java.util.List;
import java.util.Optional;

public interface RbacRepository {

	List<String> rolesOf(Long userId);

	List<String> permissionsOf(Long userId);

	Optional<AuthPrincipal> principalOf(Long userId);

}

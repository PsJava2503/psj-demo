package com.commerce.user.application.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.session.SaSession;
import com.commerce.security.AuthSession;
import com.commerce.user.application.port.AuthUseCase;
import com.commerce.user.domain.model.AuthPrincipal;
import com.commerce.user.domain.model.Role;
import com.commerce.user.domain.model.RoleQueryOptions;
import com.commerce.user.domain.model.User;
import com.commerce.user.domain.model.UserCredential;
import com.commerce.user.domain.model.UserCredentialQueryOptions;
import com.commerce.user.domain.model.UserQueryOptions;
import com.commerce.user.domain.model.UserRoleAssignment;
import com.commerce.user.domain.port.CredentialRepository;
import com.commerce.user.domain.port.RbacRepository;
import com.commerce.user.domain.service.CredentialDomainService;
import com.commerce.user.domain.service.RoleDomainService;
import com.commerce.user.domain.service.UserDomainService;
import com.commerce.user.domain.service.UserRoleDomainService;
import com.commerce.user.interfaces.rest.ChangePasswordRequest;
import com.commerce.user.interfaces.rest.DisableLoginRequest;
import com.commerce.user.interfaces.rest.LoginRequest;
import com.commerce.user.interfaces.rest.LoginResponse;
import com.commerce.user.interfaces.rest.RegisterRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthApplicationService implements AuthUseCase {

	private static final String PASSWORD_ALGORITHM = "SHA-256";
	private static final String DEFAULT_REGISTER_ROLE = "CUSTOMER";

	private final CredentialRepository credentialRepository;
	private final RbacRepository rbacRepository;
	private final UserDomainService userDomainService;
	private final CredentialDomainService credentialDomainService;
	private final RoleDomainService roleDomainService;
	private final UserRoleDomainService userRoleDomainService;

	public AuthApplicationService(
			CredentialRepository credentialRepository,
			RbacRepository rbacRepository,
			UserDomainService userDomainService,
			CredentialDomainService credentialDomainService,
			RoleDomainService roleDomainService,
			UserRoleDomainService userRoleDomainService
	) {
		this.credentialRepository = credentialRepository;
		this.rbacRepository = rbacRepository;
		this.userDomainService = userDomainService;
		this.credentialDomainService = credentialDomainService;
		this.roleDomainService = roleDomainService;
		this.userRoleDomainService = userRoleDomainService;
	}

	@Override
	@Transactional
	public LoginResponse register(RegisterRequest request) {
		requireText(request.firstName(), "firstName");
		requireText(request.secondName(), "secondName");
		requireText(request.phone(), "phone");
		requireText(request.email(), "email");
		requireText(request.username(), "username");
		requireText(request.password(), "password");
		ensureUsernameAvailable(request.username());

		ZonedDateTime now = ZonedDateTime.now();
		User user = new User(
				null,
				request.firstName(),
				request.secondName(),
				request.phone(),
				request.email(),
				null,
				List.of(),
				List.of(),
				true,
				false,
				now,
				now
		);
		userDomainService.create(user);
		User createdUser = queryCreatedUser(request.phone(), request.email());

		String salt = UUID.randomUUID().toString();
		credentialDomainService.create(new UserCredential(
				createdUser.id(),
				request.username(),
				hash(salt, request.password()),
				salt,
				PASSWORD_ALGORITHM,
				true,
				now,
				now
		));
		userRoleDomainService.create(new UserRoleAssignment(createdUser.id(), defaultRole().id(), now, null));
		return login(new LoginRequest(request.username(), request.password()));
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		UserCredential credential = credentialRepository.findByLogin(request.username())
				.filter(userCredential -> matches(request.password(), userCredential))
				.orElse(null);
		if (credential == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid username or password");
		}
		if (!Boolean.TRUE.equals(credential.enabled()) || !activeUserExists(credential.userId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user disabled");
		}
		AuthSession session = sessionOf(credential.userId());
		StpUtil.login(credential.userId());
		SaSession tokenSession = StpUtil.getTokenSession();
		tokenSession.set("userId", session.userId());
		tokenSession.set("username", session.username());
		tokenSession.set("roles", session.roles());
		tokenSession.set("permissions", session.permissions());
		return LoginResponse.of(session, StpUtil.getTokenValue());
	}

	@Override
	@Transactional
	public void changePassword(ChangePasswordRequest request) {
		StpUtil.checkLogin();
		Long userId = StpUtil.getLoginIdAsLong();
		UserCredential credential = credentialDomainService.query(new UserCredentialQueryOptions(
						Optional.of(userId), Optional.empty(), Optional.of(true)
				)).stream()
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "credential not found"));
		if (!matches(request.oldPassword(), credential)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid password");
		}
		requireText(request.newPassword(), "newPassword");
		String salt = UUID.randomUUID().toString();
		credentialDomainService.update(new UserCredential(
				userId,
				credential.username(),
				hash(salt, request.newPassword()),
				salt,
				PASSWORD_ALGORITHM,
				true,
				credential.createTime(),
				ZonedDateTime.now()
		));
		StpUtil.logout(userId);
	}

	@Override
	@Transactional
	public void disableLogin(DisableLoginRequest request) {
		if (request.userId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
		}
		credentialDomainService.delete(request.userId());
		StpUtil.logout(request.userId());
	}

	private boolean activeUserExists(Long userId) {
		UserQueryOptions options = new UserQueryOptions(
				Optional.of(userId),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(true),
				Optional.of(false),
				Optional.empty(),
				Optional.empty()
		);
		return userDomainService.query(options).stream()
				.findFirst()
				.map(User::id)
				.isPresent();
	}

	private void ensureUsernameAvailable(String username) {
		boolean exists = credentialDomainService.query(new UserCredentialQueryOptions(
						Optional.empty(), Optional.of(username), Optional.empty()
				)).stream()
				.findFirst()
				.isPresent();
		if (exists) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "username already exists");
		}
	}

	private User queryCreatedUser(String phone, String email) {
		return userDomainService.query(new UserQueryOptions(
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.of(phone),
						Optional.of(email),
						Optional.empty(),
						Optional.of(true),
						Optional.of(false),
						Optional.empty(),
						Optional.empty()
				)).stream()
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "user create failed"));
	}

	private Role defaultRole() {
		return roleDomainService.query(new RoleQueryOptions(
						Optional.empty(), Optional.of(DEFAULT_REGISTER_ROLE), Optional.of(true), Optional.of(false)
				)).stream()
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "default role not found"));
	}

	private void requireText(String value, String name) {
		if (value == null || value.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, name + " is required");
		}
	}

	@Override
	public AuthSession session() {
		StpUtil.checkLogin();
		SaSession tokenSession = StpUtil.getTokenSession();
		Object userId = tokenSession.get("userId");
		Object username = tokenSession.get("username");
		if (userId != null && username != null) {
			return new AuthSession(
					Long.valueOf(String.valueOf(userId)),
					String.valueOf(username),
					asStringList(tokenSession.get("roles")),
					asStringList(tokenSession.get("permissions"))
			);
		}
		return sessionOf(StpUtil.getLoginIdAsLong());
	}

	@Override
	public void logout() {
		StpUtil.logout();
	}

	private AuthSession sessionOf(Long userId) {
		AuthPrincipal principal = rbacRepository.principalOf(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
		return new AuthSession(principal.userId(), principal.username(), principal.roles(), principal.permissions());
	}

	private List<String> asStringList(Object value) {
		if (value instanceof List<?> list) {
			return list.stream().map(String::valueOf).toList();
		}
		return List.of();
	}

	private boolean matches(String rawPassword, UserCredential credential) {
		if (rawPassword == null || !PASSWORD_ALGORITHM.equalsIgnoreCase(credential.passwordAlgorithm())) {
			return false;
		}
		return hash(credential.passwordSalt(), rawPassword).equalsIgnoreCase(credential.passwordHash());
	}

	private String hash(String salt, String rawPassword) {
		try {
			MessageDigest digest = MessageDigest.getInstance(PASSWORD_ALGORITHM);
			byte[] hashed = digest.digest((salt + rawPassword).getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashed);
		}
		catch (NoSuchAlgorithmException error) {
			throw new IllegalStateException("password algorithm unavailable", error);
		}
	}

}

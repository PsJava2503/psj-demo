package com.commerce.user.domain.port;

import com.commerce.user.domain.model.UserCredential;
import com.commerce.user.domain.model.UserCredentialQueryOptions;
import java.util.List;
import java.util.Optional;

public interface CredentialRepository {

	int create(UserCredential credential);

	int update(UserCredential credential);

	int delete(Long userId);

	List<UserCredential> query(UserCredentialQueryOptions options);

	Optional<UserCredential> findByLogin(String login);

}

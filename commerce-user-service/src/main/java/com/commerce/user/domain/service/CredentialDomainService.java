package com.commerce.user.domain.service;

import com.commerce.user.domain.model.UserCredential;
import com.commerce.user.domain.model.UserCredentialQueryOptions;
import com.commerce.user.domain.port.CredentialRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CredentialDomainService {

	private final CredentialRepository credentialRepository;

	public CredentialDomainService(CredentialRepository credentialRepository) {
		this.credentialRepository = credentialRepository;
	}

	public int create(UserCredential credential) {
		return credentialRepository.create(credential);
	}

	public int update(UserCredential credential) {
		return credentialRepository.update(credential);
	}

	public int delete(Long userId) {
		return credentialRepository.delete(userId);
	}

	public List<UserCredential> query(UserCredentialQueryOptions options) {
		return credentialRepository.query(options);
	}

}

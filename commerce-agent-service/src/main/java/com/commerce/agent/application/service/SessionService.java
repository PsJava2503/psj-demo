package com.commerce.agent.application.service;

import com.commerce.agent.application.model.ChatSession;
import com.commerce.agent.config.AgentProperties;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SessionService {

	private final AgentProperties properties;

	private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

	public SessionService(AgentProperties properties) {
		this.properties = properties;
	}

	public ChatSession getOrCreate(String requestedId) {
		String sessionId = StringUtils.hasText(requestedId) ? requestedId : UUID.randomUUID().toString();
		return sessions.computeIfAbsent(
				sessionId,
				id -> new ChatSession(id, properties.getSession().getMaxPairs())
		);
	}

	public boolean clear(String sessionId) {
		ChatSession session = sessions.get(sessionId);
		if (session == null) {
			return false;
		}
		session.clear();
		return true;
	}
}

package com.commerce.agent.application.service;

import com.commerce.agent.application.model.ChatSession;
import com.commerce.agent.config.AgentProperties;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SessionService {

	private final AgentProperties properties;

	private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

	private final Map<String, ReentrantLock> sessionLocks = new ConcurrentHashMap<>();

	public SessionService(AgentProperties properties) {
		this.properties = properties;
	}

	public ChatSession getOrCreate(String requestedId) {
		return getOrCreate(requestedId, null);
	}

	public ChatSession getOrCreate(String requestedId, Long ownerUserId) {
		String sessionId = StringUtils.hasText(requestedId) ? requestedId : UUID.randomUUID().toString();
		return sessions.computeIfAbsent(storageKey(sessionId, ownerUserId),
				ignored -> new ChatSession(sessionId, properties.getSession().getMaxPairs()));
	}

	public boolean clear(String sessionId) {
		return clear(sessionId, null);
	}

	public boolean clear(String sessionId, Long ownerUserId) {
		ChatSession session = sessions.get(storageKey(sessionId, ownerUserId));
		if (session == null) {
			return false;
		}
		session.clear();
		return true;
	}

	public SessionLease acquire(String sessionId, long timeoutMs) {
		return acquire(sessionId, null, timeoutMs);
	}

	public SessionLease acquire(String sessionId, Long ownerUserId, long timeoutMs) {
		ReentrantLock lock = sessionLocks.computeIfAbsent(storageKey(sessionId, ownerUserId),
				ignored -> new ReentrantLock());
		try {
			if (!lock.tryLock(Math.max(1, timeoutMs), TimeUnit.MILLISECONDS)) {
				throw new IllegalStateException("同一会话正在处理其他请求，请稍后重试");
			}
			return lock::unlock;
		} catch (InterruptedException error) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("等待会话执行锁时被中断", error);
		}
	}

	private String storageKey(String sessionId, Long ownerUserId) {
		String owner = ownerUserId == null ? "anonymous" : ownerUserId.toString();
		return owner + ":" + sessionId;
	}

	@FunctionalInterface
	public interface SessionLease extends AutoCloseable {

		@Override
		void close();
	}
}

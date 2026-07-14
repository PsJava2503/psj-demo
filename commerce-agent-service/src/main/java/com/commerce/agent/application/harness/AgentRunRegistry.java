package com.commerce.agent.application.harness;

import com.commerce.agent.application.service.UserContextService;
import com.commerce.agent.config.AgentProperties;
import com.commerce.security.CommerceRoles;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

@Service
public class AgentRunRegistry {

	private final AgentProperties properties;

	private final ConcurrentMap<String, AgentRun> runs = new ConcurrentHashMap<>();

	public AgentRunRegistry(AgentProperties properties) {
		this.properties = properties;
	}

	AgentRun create(String sessionId, Long ownerUserId, String question, Consumer<AgentRunEvent> eventConsumer) {
		trimCompletedRuns();
		AgentProperties.Harness harness = properties.getHarness();
		AgentRun run = new AgentRun(UUID.randomUUID().toString(), sessionId, ownerUserId, question,
				harness.getMaxEventsPerRun(), harness.getMaxToolCalls(), harness.getMaxOutputChars(),
				harness.getMaxDurationMs(), eventConsumer);
		runs.put(run.runId(), run);
		return run;
	}

	public Optional<AgentRunSnapshot> find(String runId, UserContextService.UserContext requester) {
		return Optional.ofNullable(runs.get(runId)).filter(run -> canAccess(run, requester)).map(AgentRun::snapshot);
	}

	public List<AgentRunSnapshot> list(String sessionId, int requestedLimit, UserContextService.UserContext requester) {
		int limit = Math.max(1, Math.min(requestedLimit, 100));
		return runs.values().stream().filter(run -> canAccess(run, requester))
				.filter(run -> sessionId == null || sessionId.isBlank() || sessionId.equals(run.sessionId()))
				.sorted(Comparator.comparing(AgentRun::queuedAt).reversed()).limit(limit).map(AgentRun::snapshot)
				.toList();
	}

	public boolean cancel(String runId, String reason, UserContextService.UserContext requester) {
		AgentRun run = runs.get(runId);
		return run != null && canAccess(run, requester) && run.cancel(reason);
	}

	private boolean canAccess(AgentRun run, UserContextService.UserContext requester) {
		UserContextService.UserContext context = requester == null ? UserContextService.UserContext.empty() : requester;
		if (context.hasRole(CommerceRoles.ADMIN)) {
			return true;
		}
		return Objects.equals(run.ownerUserId(), context.userId());
	}

	private synchronized void trimCompletedRuns() {
		int maxRuns = Math.max(10, properties.getHarness().getMaxRetainedRuns());
		int removeCount = runs.size() - maxRuns + 1;
		if (removeCount <= 0) {
			return;
		}
		runs.values().stream().filter(run -> run.snapshot().status().isTerminal())
				.sorted(Comparator.comparing(AgentRun::queuedAt)).limit(removeCount).map(AgentRun::runId)
				.forEach(runs::remove);
	}
}

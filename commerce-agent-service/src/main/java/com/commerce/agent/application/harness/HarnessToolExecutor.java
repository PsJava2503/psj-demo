package com.commerce.agent.application.harness;

import com.commerce.agent.config.AgentProperties;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class HarnessToolExecutor {

	private final AgentProperties properties;

	private final ThreadLocal<AgentRun> currentRun = new ThreadLocal<>();

	public HarnessToolExecutor(AgentProperties properties) {
		this.properties = properties;
	}

	Scope open(AgentRun run) {
		AgentRun previous = currentRun.get();
		currentRun.set(run);
		return () -> {
			if (previous == null) {
				currentRun.remove();
			} else {
				currentRun.set(previous);
			}
		};
	}

	public <T> T execute(String toolName, Map<String, Object> input, Supplier<T> invocation) {
		AgentRun run = currentRun.get();
		if (run == null) {
			return invocation.get();
		}
		int call = run.beginTool(toolName, input);
		long startedAt = System.nanoTime();
		try {
			T result = invocation.get();
			run.toolCompleted(toolName, call, elapsedMs(startedAt), preview(result));
			return result;
		} catch (RuntimeException error) {
			run.toolFailed(toolName, call, elapsedMs(startedAt), error);
			throw error;
		}
	}

	private long elapsedMs(long startedAt) {
		return Math.max(0, (System.nanoTime() - startedAt) / 1_000_000);
	}

	private String preview(Object result) {
		String value = String.valueOf(result);
		int maxChars = Math.max(0, properties.getHarness().getToolResultPreviewChars());
		if (value.length() <= maxChars) {
			return value;
		}
		return value.substring(0, maxChars) + "...";
	}

	@FunctionalInterface
	interface Scope extends AutoCloseable {

		@Override
		void close();
	}
}

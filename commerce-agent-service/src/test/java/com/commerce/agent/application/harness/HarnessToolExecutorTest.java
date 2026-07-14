package com.commerce.agent.application.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.commerce.agent.config.AgentProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HarnessToolExecutorTest {

	@Test
	void auditsToolsAndEnforcesCallBudget() {
		AgentProperties properties = new AgentProperties();
		properties.getHarness().setMaxToolCalls(1);
		AgentRunRegistry registry = new AgentRunRegistry(properties);
		AgentRun run = registry.create("session", 1L, "question", ignored -> {
		});
		HarnessToolExecutor executor = new HarnessToolExecutor(properties);
		run.start();

		try (HarnessToolExecutor.Scope ignored = executor.open(run)) {
			assertThat(executor.execute("firstTool", Map.of("id", 1), () -> "ok")).isEqualTo("ok");
			assertThatThrownBy(() -> executor.execute("secondTool", Map.of(), () -> "never"))
					.isInstanceOf(AgentBudgetExceededException.class).hasMessageContaining("工具调用次数超过上限");
		}

		assertThat(run.snapshot().events()).extracting(AgentRunEvent::type).contains(AgentEventType.TOOL_STARTED,
				AgentEventType.TOOL_COMPLETED);
	}
}

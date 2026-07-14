package com.commerce.agent.application.harness;

import com.commerce.agent.application.service.AgentRoutingService;
import com.commerce.agent.application.service.UserContextService;
import java.util.function.Consumer;

public record AgentRunCommand(String requestedSessionId, String question, UserContextService.UserContext userContext,
		AgentRoutingService.AgentMode requestedMode, boolean streaming, Consumer<String> outputConsumer,
		Consumer<AgentRunEvent> eventConsumer) {

	public AgentRunCommand {
		userContext = userContext == null ? UserContextService.UserContext.empty() : userContext;
		outputConsumer = outputConsumer == null ? ignored -> {
		} : outputConsumer;
		eventConsumer = eventConsumer == null ? ignored -> {
		} : eventConsumer;
	}
}

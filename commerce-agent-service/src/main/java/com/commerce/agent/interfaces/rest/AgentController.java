package com.commerce.agent.interfaces.rest;

import com.commerce.agent.application.model.ChatSession;
import com.commerce.agent.application.service.AgentRoutingService;
import com.commerce.agent.application.service.ChatAgentService;
import com.commerce.agent.application.service.KnowledgeIngestionService;
import com.commerce.agent.application.service.SessionService;
import com.commerce.agent.application.service.SupervisorWorkflowService;
import com.commerce.agent.application.service.UserContextService;
import com.commerce.agent.interfaces.rest.dto.AgentChatRequest;
import com.commerce.agent.interfaces.rest.dto.AgentResponse;
import com.commerce.agent.interfaces.rest.dto.BusinessOpsRequest;
import com.commerce.agent.interfaces.rest.dto.ClearSessionRequest;
import com.commerce.agent.interfaces.rest.dto.KnowledgeUploadResponse;
import com.commerce.agent.interfaces.rest.dto.SseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

	private final ChatAgentService chatAgentService;

	private final AgentRoutingService agentRoutingService;

	private final SupervisorWorkflowService supervisorWorkflowService;

	private final KnowledgeIngestionService knowledgeIngestionService;

	private final SessionService sessionService;

	private final UserContextService userContextService;

	private final ExecutorService executor = Executors.newCachedThreadPool();

	public AgentController(
			ChatAgentService chatAgentService,
			AgentRoutingService agentRoutingService,
			SupervisorWorkflowService supervisorWorkflowService,
			KnowledgeIngestionService knowledgeIngestionService,
			SessionService sessionService,
			UserContextService userContextService
	) {
		this.chatAgentService = chatAgentService;
		this.agentRoutingService = agentRoutingService;
		this.supervisorWorkflowService = supervisorWorkflowService;
		this.knowledgeIngestionService = knowledgeIngestionService;
		this.sessionService = sessionService;
		this.userContextService = userContextService;
	}

	@PostMapping("/chat")
	public AgentResponse chat(@Valid @RequestBody AgentChatRequest request, HttpServletRequest httpRequest) {
		userContextService.from(httpRequest);
		try {
			ChatSession session = sessionService.getOrCreate(request.sessionId());
			String answer = answer(request.question(), session);
			session.addExchange(request.question(), answer);
			return AgentResponse.success(session.id(), answer);
		}
		catch (Exception error) {
			return AgentResponse.error(request.sessionId(), error.getMessage());
		}
		finally {
			userContextService.clear();
		}
	}

	@PostMapping(value = "/chat_stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter chatStream(@Valid @RequestBody AgentChatRequest request, HttpServletRequest httpRequest) {
		SseEmitter emitter = new SseEmitter(300000L);
		UserContextService.UserContext context = userContextService.from(httpRequest);
		executor.execute(() -> {
			userContextService.set(context);
			try {
				ChatSession session = sessionService.getOrCreate(request.sessionId());
				String answer = streamAnswer(request.question(), session, emitter);
				session.addExchange(request.question(), answer);
				send(emitter, SseMessage.done());
				emitter.complete();
			}
			catch (Exception error) {
				send(emitter, SseMessage.error(error.getMessage()));
				emitter.completeWithError(error);
			}
			finally {
				userContextService.clear();
			}
		});
		return emitter;
	}

	@PostMapping(value = "/business_ops", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Deprecated
	public SseEmitter businessOps(@RequestBody(required = false) BusinessOpsRequest request, HttpServletRequest httpRequest) {
		SseEmitter emitter = new SseEmitter(600000L);
		UserContextService.UserContext context = userContextService.from(httpRequest);
		executor.execute(() -> {
			userContextService.set(context);
			try {
				String task = request == null ? null : request.task();
				send(emitter, SseMessage.content("正在启动 Planner/Executor/Supervisor 多 Agent 分析...\n"));
				String report = supervisorWorkflowService.analyze(task);
				for (String chunk : report.split("(?<=\\n)")) {
					if (!chunk.isBlank()) {
						send(emitter, SseMessage.content(chunk));
					}
				}
				send(emitter, SseMessage.done());
				emitter.complete();
			}
			catch (Exception error) {
				send(emitter, SseMessage.error(error.getMessage()));
				emitter.completeWithError(error);
			}
			finally {
				userContextService.clear();
			}
		});
		return emitter;
	}

	@PostMapping(value = "/knowledge/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public KnowledgeUploadResponse upload(@RequestPart("file") MultipartFile file) throws IOException {
		return knowledgeIngestionService.upload(file);
	}

	@PostMapping("/chat/clear")
	public ResponseEntity<Map<String, Object>> clear(@Valid @RequestBody ClearSessionRequest request) {
		return ResponseEntity.ok(Map.of(
				"success", sessionService.clear(request.sessionId()),
				"sessionId", request.sessionId()
		));
	}

	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("status", "UP", "service", "commerce-agent-service");
	}

	private void send(SseEmitter emitter, SseMessage message) {
		try {
			emitter.send(SseEmitter.event().name("message").data(message, MediaType.APPLICATION_JSON));
		}
		catch (IOException error) {
			throw new IllegalStateException("Failed to send SSE message", error);
		}
	}

	private String answer(String question, ChatSession session) {
		AgentRoutingService.AgentRoute route = agentRoutingService.route(question);
		if (route.isBusinessWorkflow()) {
			return supervisorWorkflowService.analyze(question);
		}
		return chatAgentService.chat(question, session.history());
	}

	private String streamAnswer(String question, ChatSession session, SseEmitter emitter) {
		AgentRoutingService.AgentRoute route = agentRoutingService.route(question);
		if (route.isBusinessWorkflow()) {
			send(emitter, SseMessage.content("已识别为复杂业务分析任务，正在启动多 Agent 分析...\n"));
			String report = supervisorWorkflowService.analyze(question);
			for (String chunk : report.split("(?<=\\n)")) {
				if (!chunk.isBlank()) {
					send(emitter, SseMessage.content(chunk));
				}
			}
			return report;
		}
		return chatAgentService.stream(
				question,
				session.history(),
				chunk -> send(emitter, SseMessage.content(chunk))
		);
	}
}

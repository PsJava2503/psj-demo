package com.commerce.agent.interfaces.rest;

import com.commerce.agent.application.harness.AgentHarnessResult;
import com.commerce.agent.application.harness.AgentHarnessService;
import com.commerce.agent.application.harness.AgentRunCommand;
import com.commerce.agent.application.harness.AgentRunRegistry;
import com.commerce.agent.application.harness.AgentRunSnapshot;
import com.commerce.agent.application.service.AgentRoutingService;
import com.commerce.agent.application.service.KnowledgeIngestionService;
import com.commerce.agent.application.service.SessionService;
import com.commerce.agent.application.service.UserContextService;
import com.commerce.agent.interfaces.rest.dto.AgentChatRequest;
import com.commerce.agent.interfaces.rest.dto.AgentResponse;
import com.commerce.agent.interfaces.rest.dto.BusinessOpsRequest;
import com.commerce.agent.interfaces.rest.dto.ClearSessionRequest;
import com.commerce.agent.interfaces.rest.dto.KnowledgeUploadResponse;
import com.commerce.agent.interfaces.rest.dto.SseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

	private final AgentHarnessService harnessService;

	private final AgentRunRegistry runRegistry;

	private final KnowledgeIngestionService knowledgeIngestionService;

	private final SessionService sessionService;

	private final UserContextService userContextService;

	private final ExecutorService executor = Executors.newCachedThreadPool();

	public AgentController(AgentHarnessService harnessService, AgentRunRegistry runRegistry,
			KnowledgeIngestionService knowledgeIngestionService, SessionService sessionService,
			UserContextService userContextService) {
		this.harnessService = harnessService;
		this.runRegistry = runRegistry;
		this.knowledgeIngestionService = knowledgeIngestionService;
		this.sessionService = sessionService;
		this.userContextService = userContextService;
	}

	@PostMapping("/chat")
	public AgentResponse chat(@Valid @RequestBody AgentChatRequest request, HttpServletRequest httpRequest) {
		UserContextService.UserContext context = userContextService.from(httpRequest);
		try {
			AgentHarnessResult result = harnessService.run(
					new AgentRunCommand(request.sessionId(), request.question(), context, null, false, null, null));
			return toResponse(result);
		} finally {
			userContextService.clear();
		}
	}

	@PostMapping(value = "/chat_stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter chatStream(@Valid @RequestBody AgentChatRequest request, HttpServletRequest httpRequest) {
		SseEmitter emitter = new SseEmitter(300000L);
		UserContextService.UserContext context = userContextService.from(httpRequest);
		userContextService.clear();
		executor.execute(() -> {
			try {
				AgentHarnessResult result = harnessService.run(new AgentRunCommand(request.sessionId(),
						request.question(), context, null, true, chunk -> send(emitter, SseMessage.content(chunk)),
						event -> send(emitter, SseMessage.status(event))));
				finishStream(emitter, result);
			} catch (RuntimeException error) {
				safeError(emitter, error.getMessage());
			}
		});
		return emitter;
	}

	@PostMapping(value = "/business_ops", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Deprecated
	public SseEmitter businessOps(@RequestBody(required = false) BusinessOpsRequest request,
			HttpServletRequest httpRequest) {
		SseEmitter emitter = new SseEmitter(600000L);
		UserContextService.UserContext context = userContextService.from(httpRequest);
		userContextService.clear();
		executor.execute(() -> {
			try {
				String task = request == null ? null : request.task();
				String requestedSessionId = request == null ? null : request.sessionId();
				AgentHarnessResult result = harnessService.run(new AgentRunCommand(requestedSessionId,
						task == null || task.isBlank() ? "请分析当前电商系统可能存在的业务风险" : task, context,
						AgentRoutingService.AgentMode.BUSINESS_WORKFLOW, true,
						chunk -> send(emitter, SseMessage.content(chunk)),
						event -> send(emitter, SseMessage.status(event))));
				finishStream(emitter, result);
			} catch (RuntimeException error) {
				safeError(emitter, error.getMessage());
			}
		});
		return emitter;
	}

	@PostMapping(value = "/knowledge/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public KnowledgeUploadResponse upload(@RequestPart("file") MultipartFile file) throws IOException {
		return knowledgeIngestionService.upload(file);
	}

	@PostMapping("/chat/clear")
	public ResponseEntity<Map<String, Object>> clear(@Valid @RequestBody ClearSessionRequest request,
			HttpServletRequest httpRequest) {
		UserContextService.UserContext context = userContextService.from(httpRequest);
		try {
			return ResponseEntity.ok(Map.of("success", sessionService.clear(request.sessionId(), context.userId()),
					"sessionId", request.sessionId()));
		} finally {
			userContextService.clear();
		}
	}

	@GetMapping("/runs/{runId}")
	public ResponseEntity<AgentRunSnapshot> getRun(@PathVariable String runId, HttpServletRequest request) {
		UserContextService.UserContext context = userContextService.from(request);
		try {
			return runRegistry.find(runId, context).map(ResponseEntity::ok)
					.orElseGet(() -> ResponseEntity.notFound().build());
		} finally {
			userContextService.clear();
		}
	}

	@GetMapping("/runs")
	public List<AgentRunSnapshot> listRuns(@RequestParam(required = false) String sessionId,
			@RequestParam(defaultValue = "20") int limit, HttpServletRequest request) {
		UserContextService.UserContext context = userContextService.from(request);
		try {
			return runRegistry.list(sessionId, limit, context);
		} finally {
			userContextService.clear();
		}
	}

	@PostMapping("/runs/{runId}/cancel")
	public ResponseEntity<Map<String, Object>> cancelRun(@PathVariable String runId, HttpServletRequest request) {
		UserContextService.UserContext context = userContextService.from(request);
		try {
			boolean cancelled = runRegistry.cancel(runId, "用户请求取消", context);
			return ResponseEntity.ok(Map.of("runId", runId, "cancelled", cancelled));
		} finally {
			userContextService.clear();
		}
	}

	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("status", "UP", "service", "commerce-agent-service", "runtime", "harness");
	}

	@PreDestroy
	public void shutdown() {
		executor.shutdownNow();
	}

	private void send(SseEmitter emitter, SseMessage message) {
		try {
			emitter.send(SseEmitter.event().name("message").data(message, MediaType.APPLICATION_JSON));
		} catch (IOException error) {
			throw new IllegalStateException("Failed to send SSE message", error);
		}
	}

	private AgentResponse toResponse(AgentHarnessResult result) {
		if (result.success()) {
			return AgentResponse.success(result.sessionId(), result.runId(), result.answer());
		}
		return AgentResponse.error(result.sessionId(), result.runId(), result.error());
	}

	private void finishStream(SseEmitter emitter, AgentHarnessResult result) {
		if (!result.success()) {
			safeError(emitter, result.error());
			return;
		}
		send(emitter, SseMessage.done());
		emitter.complete();
	}

	private void safeError(SseEmitter emitter, String message) {
		try {
			send(emitter, SseMessage.error(message == null ? "Agent 运行失败" : message));
		} catch (RuntimeException ignored) {
			// The client may already have disconnected.
		}
		emitter.complete();
	}
}

package com.commerce.agent.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agent")
public class AgentProperties {

	private boolean mockEnabled = true;

	private ModelProvider modelProvider = ModelProvider.DASHSCOPE;

	private OpenAi openai = new OpenAi();

	private Session session = new Session();

	private ModelOptions chat = new ModelOptions();

	private ModelOptions workflow = new ModelOptions();

	private Harness harness = new Harness();

	private Rag rag = new Rag();

	public boolean isMockEnabled() {
		return mockEnabled;
	}

	public void setMockEnabled(boolean mockEnabled) {
		this.mockEnabled = mockEnabled;
	}

	public ModelProvider getModelProvider() {
		return modelProvider;
	}

	public void setModelProvider(ModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

	public OpenAi getOpenai() {
		return openai;
	}

	public void setOpenai(OpenAi openai) {
		this.openai = openai;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public ModelOptions getChat() {
		return chat;
	}

	public void setChat(ModelOptions chat) {
		this.chat = chat;
	}

	public ModelOptions getWorkflow() {
		return workflow;
	}

	public void setWorkflow(ModelOptions workflow) {
		this.workflow = workflow;
	}

	public Harness getHarness() {
		return harness;
	}

	public void setHarness(Harness harness) {
		this.harness = harness;
	}

	public Rag getRag() {
		return rag;
	}

	public void setRag(Rag rag) {
		this.rag = rag;
	}

	public static class Session {

		private int maxPairs = 6;

		public int getMaxPairs() {
			return maxPairs;
		}

		public void setMaxPairs(int maxPairs) {
			this.maxPairs = maxPairs;
		}
	}

	public static class OpenAi {

		private String apiKey = "mock-api-key";

		private String baseUrl = "https://api.openai.com";

		private String completionsPath = "/v1/chat/completions";

		public String getApiKey() {
			return apiKey;
		}

		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public String getCompletionsPath() {
			return completionsPath;
		}

		public void setCompletionsPath(String completionsPath) {
			this.completionsPath = completionsPath;
		}
	}

	public static class Harness {

		private long maxDurationMs = 180000;

		private int maxToolCalls = 12;

		private int maxOutputChars = 30000;

		private int maxEventsPerRun = 200;

		private int maxRetainedRuns = 1000;

		private int maxConcurrentRuns = 8;

		private long sessionLockTimeoutMs = 5000;

		private int toolResultPreviewChars = 500;

		public long getMaxDurationMs() {
			return maxDurationMs;
		}

		public void setMaxDurationMs(long maxDurationMs) {
			this.maxDurationMs = maxDurationMs;
		}

		public int getMaxToolCalls() {
			return maxToolCalls;
		}

		public void setMaxToolCalls(int maxToolCalls) {
			this.maxToolCalls = maxToolCalls;
		}

		public int getMaxOutputChars() {
			return maxOutputChars;
		}

		public void setMaxOutputChars(int maxOutputChars) {
			this.maxOutputChars = maxOutputChars;
		}

		public int getMaxEventsPerRun() {
			return maxEventsPerRun;
		}

		public void setMaxEventsPerRun(int maxEventsPerRun) {
			this.maxEventsPerRun = maxEventsPerRun;
		}

		public int getMaxRetainedRuns() {
			return maxRetainedRuns;
		}

		public void setMaxRetainedRuns(int maxRetainedRuns) {
			this.maxRetainedRuns = maxRetainedRuns;
		}

		public int getMaxConcurrentRuns() {
			return maxConcurrentRuns;
		}

		public void setMaxConcurrentRuns(int maxConcurrentRuns) {
			this.maxConcurrentRuns = maxConcurrentRuns;
		}

		public long getSessionLockTimeoutMs() {
			return sessionLockTimeoutMs;
		}

		public void setSessionLockTimeoutMs(long sessionLockTimeoutMs) {
			this.sessionLockTimeoutMs = sessionLockTimeoutMs;
		}

		public int getToolResultPreviewChars() {
			return toolResultPreviewChars;
		}

		public void setToolResultPreviewChars(int toolResultPreviewChars) {
			this.toolResultPreviewChars = toolResultPreviewChars;
		}
	}

	public enum ModelProvider {
		DASHSCOPE, OPENAI
	}

	public static class ModelOptions {

		private String model = "qwen-plus";

		private Double temperature = 0.7;

		private Integer maxToken = 2000;

		private Double topP = 0.9;

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public Double getTemperature() {
			return temperature;
		}

		public void setTemperature(Double temperature) {
			this.temperature = temperature;
		}

		public Integer getMaxToken() {
			return maxToken;
		}

		public void setMaxToken(Integer maxToken) {
			this.maxToken = maxToken;
		}

		public Double getTopP() {
			return topP;
		}

		public void setTopP(Double topP) {
			this.topP = topP;
		}
	}

	public static class Rag {

		private boolean enabled = true;

		private boolean useMilvus;

		private int topK = 3;

		private String uploadPath = "./uploads/agent";

		private List<String> allowedExtensions = List.of("txt", "md");

		private String embeddingModel = "text-embedding-v4";

		private Chunk chunk = new Chunk();

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isUseMilvus() {
			return useMilvus;
		}

		public void setUseMilvus(boolean useMilvus) {
			this.useMilvus = useMilvus;
		}

		public int getTopK() {
			return topK;
		}

		public void setTopK(int topK) {
			this.topK = topK;
		}

		public String getUploadPath() {
			return uploadPath;
		}

		public void setUploadPath(String uploadPath) {
			this.uploadPath = uploadPath;
		}

		public List<String> getAllowedExtensions() {
			return allowedExtensions;
		}

		public void setAllowedExtensions(List<String> allowedExtensions) {
			this.allowedExtensions = allowedExtensions;
		}

		public String getEmbeddingModel() {
			return embeddingModel;
		}

		public void setEmbeddingModel(String embeddingModel) {
			this.embeddingModel = embeddingModel;
		}

		public Chunk getChunk() {
			return chunk;
		}

		public void setChunk(Chunk chunk) {
			this.chunk = chunk;
		}
	}

	public static class Chunk {

		private int maxSize = 800;

		private int overlap = 100;

		public int getMaxSize() {
			return maxSize;
		}

		public void setMaxSize(int maxSize) {
			this.maxSize = maxSize;
		}

		public int getOverlap() {
			return overlap;
		}

		public void setOverlap(int overlap) {
			this.overlap = overlap;
		}
	}
}

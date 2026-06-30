package com.commerce.agent.application.service;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.commerce.agent.agent.tool.CommerceTools;
import com.commerce.agent.agent.tool.DateTimeTools;
import com.commerce.agent.agent.tool.InternalDocsTools;
import com.commerce.agent.application.model.ChatMessage;
import com.commerce.agent.config.AgentProperties;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Service
public class ChatAgentService {

	private final AgentProperties properties;

	private final String apiKey;

	private final DateTimeTools dateTimeTools;

	private final InternalDocsTools internalDocsTools;

	private final CommerceTools commerceTools;

	public ChatAgentService(
			AgentProperties properties,
			@Value("${spring.ai.dashscope.api-key:}") String apiKey,
			DateTimeTools dateTimeTools,
			InternalDocsTools internalDocsTools,
			CommerceTools commerceTools
	) {
		this.properties = properties;
		this.apiKey = apiKey;
		this.dateTimeTools = dateTimeTools;
		this.internalDocsTools = internalDocsTools;
		this.commerceTools = commerceTools;
	}

	public String chat(String question, List<ChatMessage> history) {
		if (useMock()) {
			return mockAnswer(question, history);
		}
		try {
			ReactAgent agent = createReactAgent(properties.getChat(), buildChatPrompt(history));
			return agent.call(question).getText();
		}
		catch (Exception error) {
			throw new IllegalStateException("ReactAgent 对话失败: " + error.getMessage(), error);
		}
	}

	public String stream(String question, List<ChatMessage> history, Consumer<String> chunkConsumer) {
		if (useMock()) {
			String answer = mockAnswer(question, history);
			for (String chunk : answer.split("(?<=。|\\n)")) {
				if (StringUtils.hasText(chunk)) {
					chunkConsumer.accept(chunk);
				}
			}
			return answer;
		}
		try {
			ReactAgent agent = createReactAgent(properties.getChat(), buildChatPrompt(history));
			StringBuilder fullAnswer = new StringBuilder();
			Flux<NodeOutput> stream = agent.stream(question);
			stream.toStream().forEach(output -> {
				if (output instanceof StreamingOutput streamingOutput
						&& streamingOutput.getOutputType() == OutputType.AGENT_MODEL_STREAMING
						&& streamingOutput.message() != null) {
					String chunk = streamingOutput.message().getText();
					if (StringUtils.hasText(chunk)) {
						fullAnswer.append(chunk);
						chunkConsumer.accept(chunk);
					}
				}
			});
			return fullAnswer.toString();
		}
		catch (Exception error) {
			throw new IllegalStateException("ReactAgent 流式对话失败: " + error.getMessage(), error);
		}
	}

	ReactAgent createReactAgent(AgentProperties.ModelOptions options, String systemPrompt) {
		DashScopeChatModel chatModel = createChatModel(options);
		return ReactAgent.builder()
				.name("commerce_agent")
				.description("面向电商系统的工具增强 AI Agent")
				.model(chatModel)
				.systemPrompt(systemPrompt)
				.methodTools(dateTimeTools, internalDocsTools, commerceTools)
				.build();
	}

	DashScopeChatModel createChatModel(AgentProperties.ModelOptions options) {
		DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apiKey).build();
		return DashScopeChatModel.builder()
				.dashScopeApi(dashScopeApi)
				.defaultOptions(DashScopeChatOptions.builder()
						.withModel(options.getModel())
						.withTemperature(options.getTemperature())
						.withMaxToken(options.getMaxToken())
						.withTopP(options.getTopP())
						.build())
				.build();
	}

	String buildChatPrompt(List<ChatMessage> history) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("""
				你是 Commerce Demo 的 AI Agent 服务。
				你可以使用工具查询当前时间、内部知识库、商品、订单、购物车和当前用户上下文。
				回答必须基于工具或用户提供的信息；不要编造订单、库存、支付或用户数据。
				涉及写操作、支付、退款、删除、发货等高风险动作时，只能给建议，不能宣称已经执行。
				
				""");
		if (!history.isEmpty()) {
			prompt.append("--- 对话历史 ---\n");
			for (ChatMessage message : history) {
				prompt.append("user".equals(message.role()) ? "用户: " : "助手: ")
						.append(message.content())
						.append("\n");
			}
			prompt.append("--- 对话历史结束 ---\n");
		}
		return prompt.toString();
	}

	private boolean useMock() {
		return properties.isMockEnabled() || !StringUtils.hasText(apiKey) || "mock-api-key".equals(apiKey);
	}

	private String mockAnswer(String question, List<ChatMessage> history) {
		String docs = internalDocsTools.queryInternalDocs(question);
		return """
				这是 commerce-agent-service 的本地 mock 响应，真实 DashScope 调用将在配置 DASHSCOPE_API_KEY 且关闭 AGENT_MOCK_ENABLED 后启用。
				
				问题：%s
				
				当前时间：%s
				
				可用能力：
				- 查询商品、订单、购物车等电商只读数据
				- 检索上传到知识库的 Markdown/TXT 文档
				- 保留最近 %d 轮会话上下文
				
				知识库摘要：
				%s
				""".formatted(question, dateTimeTools.getCurrentDateTime(), history.size() / 2, docs);
	}
}

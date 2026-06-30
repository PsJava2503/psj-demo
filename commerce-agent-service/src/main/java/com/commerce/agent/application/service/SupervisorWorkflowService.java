package com.commerce.agent.application.service;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SupervisorAgent;
import com.commerce.agent.config.AgentProperties;
import java.util.List;
import java.util.Optional;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SupervisorWorkflowService {

	private final AgentProperties properties;

	private final ChatAgentService chatAgentService;

	private final KnowledgeSearchService knowledgeSearchService;

	public SupervisorWorkflowService(
			AgentProperties properties,
			ChatAgentService chatAgentService,
			KnowledgeSearchService knowledgeSearchService
	) {
		this.properties = properties;
		this.chatAgentService = chatAgentService;
		this.knowledgeSearchService = knowledgeSearchService;
	}

	public String analyze(String task) {
		String effectiveTask = StringUtils.hasText(task) ? task : "请分析当前电商系统可能存在的业务风险，并给出排查计划。";
		if (useMock()) {
			return mockReport(effectiveTask);
		}
		ReactAgent planner = ReactAgent.builder()
				.name("commerce_planner")
				.description("拆解电商业务分析任务并决定下一步")
				.model(chatAgentService.createChatModel(properties.getWorkflow()))
				.systemPrompt(plannerPrompt())
				.methodTools()
				.outputKey("planner_report")
				.build();
		ReactAgent executor = ReactAgent.builder()
				.name("commerce_executor")
				.description("执行 Planner 给出的单个排查步骤")
				.model(chatAgentService.createChatModel(properties.getWorkflow()))
				.systemPrompt(executorPrompt())
				.methodTools()
				.outputKey("executor_feedback")
				.build();
		SupervisorAgent supervisor = SupervisorAgent.builder()
				.name("commerce_supervisor")
				.description("调度 Planner 和 Executor 完成电商业务分析")
				.model(chatAgentService.createChatModel(properties.getWorkflow()))
				.systemPrompt(supervisorPrompt())
				.subAgents(List.of(planner, executor))
				.build();
		try {
			Optional<OverAllState> state = supervisor.invoke(effectiveTask);
			return state.flatMap(this::extractReport).orElse("多 Agent 编排未返回有效报告。");
		}
		catch (Exception error) {
			throw new IllegalStateException("多 Agent 编排失败: " + error.getMessage(), error);
		}
	}

	private Optional<String> extractReport(OverAllState state) {
		return state.value("planner_report")
				.filter(AssistantMessage.class::isInstance)
				.map(AssistantMessage.class::cast)
				.map(AssistantMessage::getText);
	}

	private boolean useMock() {
		return chatAgentService.isMockMode();
	}

	private String mockReport(String task) {
		return """
				# Commerce Agent 业务分析报告
				
				## 任务
				%s
				
				## Planner 计划
				1. 明确用户要分析的业务对象，如订单、商品、购物车或知识库文档。
				2. 使用只读工具查询相关业务数据。
				3. 结合内部知识库给出原因、风险和下一步建议。
				
				## Executor 证据
				%s
				
				## 结论
				当前运行在 mock 模式，已验证 Supervisor/Planner/Executor 的服务入口和报告结构。配置模型 API Key 并设置 AGENT_MOCK_ENABLED=false 后，可启用真实多 Agent 编排。
				""".formatted(task, knowledgeSearchService.searchAsText(task));
	}

	private String plannerPrompt() {
		return """
				你是 Commerce Planner Agent，负责分析用户输入的电商业务排查任务。
				你需要规划查询商品、订单、购物车、内部知识库等只读工具的步骤。
				当信息足够时输出 Markdown 报告，写入 planner_report；不要编造业务数据。
				""";
	}

	private String executorPrompt() {
		return """
				你是 Commerce Executor Agent，负责执行 Planner 的第一步，并把查询证据结构化反馈到 executor_feedback。
				只能做只读查询，不允许声明已经创建订单、支付、退款、删除或发货。
				""";
	}

	private String supervisorPrompt() {
		return """
				你是 Commerce Supervisor，负责在 commerce_planner 和 commerce_executor 之间调度。
				目标是生成一份 Markdown 格式的电商业务分析报告，包含任务、证据、结论、风险和建议。
				如果工具返回空结果，必须如实说明数据不足。
				""";
	}
}

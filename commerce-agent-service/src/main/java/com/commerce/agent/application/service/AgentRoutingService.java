package com.commerce.agent.application.service;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AgentRoutingService {

	private static final List<String> WORKFLOW_KEYWORDS = List.of(
			"业务分析",
			"分析报告",
			"生成报告",
			"排查",
			"诊断",
			"根因",
			"异常",
			"风险",
			"处理方案",
			"推荐策略",
			"推荐能力",
			"复盘",
			"report",
			"diagnose",
			"troubleshoot",
			"root cause",
			"analysis"
	);

	private static final List<String> COMMERCE_ANALYSIS_TARGETS = List.of(
			"订单",
			"购物车",
			"库存",
			"支付",
			"退款",
			"商品",
			"用户",
			"推荐",
			"履约"
	);

	public AgentRoute route(String question) {
		if (!StringUtils.hasText(question)) {
			return AgentRoute.chat();
		}
		String normalized = question.toLowerCase(Locale.ROOT);
		if (WORKFLOW_KEYWORDS.stream().anyMatch(normalized::contains)) {
			return AgentRoute.businessWorkflow("命中复杂业务分析关键词");
		}
		boolean asksAnalysis = normalized.contains("分析") || normalized.contains("看一下") || normalized.contains("评估");
		boolean hasCommerceTarget = COMMERCE_ANALYSIS_TARGETS.stream().anyMatch(normalized::contains);
		if (asksAnalysis && hasCommerceTarget) {
			return AgentRoute.businessWorkflow("命中电商对象分析意图");
		}
		return AgentRoute.chat();
	}

	public record AgentRoute(
			AgentMode mode,
			String reason
	) {

		public static AgentRoute chat() {
			return new AgentRoute(AgentMode.CHAT, "普通对话");
		}

		public static AgentRoute businessWorkflow(String reason) {
			return new AgentRoute(AgentMode.BUSINESS_WORKFLOW, reason);
		}

		public boolean isBusinessWorkflow() {
			return mode == AgentMode.BUSINESS_WORKFLOW;
		}
	}

	public enum AgentMode {
		CHAT,
		BUSINESS_WORKFLOW
	}
}

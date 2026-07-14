package com.commerce.agent.agent.tool;

import com.commerce.agent.application.harness.HarnessToolExecutor;
import com.commerce.agent.application.service.KnowledgeSearchService;
import java.util.Map;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class InternalDocsTools {

	private final KnowledgeSearchService knowledgeSearchService;

	private final HarnessToolExecutor toolExecutor;

	public InternalDocsTools(KnowledgeSearchService knowledgeSearchService, HarnessToolExecutor toolExecutor) {
		this.knowledgeSearchService = knowledgeSearchService;
		this.toolExecutor = toolExecutor;
	}

	@Tool(description = "查询内部知识库、业务文档、排障手册和 Agent 上传的 Markdown/TXT 文档")
	public String queryInternalDocs(@ToolParam(description = "检索关键词或问题") String query) {
		return toolExecutor.execute("queryInternalDocs", Map.of("query", query == null ? "" : query),
				() -> knowledgeSearchService.searchAsText(query));
	}
}

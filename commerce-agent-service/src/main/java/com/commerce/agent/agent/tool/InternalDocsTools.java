package com.commerce.agent.agent.tool;

import com.commerce.agent.application.service.KnowledgeSearchService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class InternalDocsTools {

	private final KnowledgeSearchService knowledgeSearchService;

	public InternalDocsTools(KnowledgeSearchService knowledgeSearchService) {
		this.knowledgeSearchService = knowledgeSearchService;
	}

	@Tool(description = "查询内部知识库、业务文档、排障手册和 Agent 上传的 Markdown/TXT 文档")
	public String queryInternalDocs(@ToolParam(description = "检索关键词或问题") String query) {
		return knowledgeSearchService.searchAsText(query);
	}
}

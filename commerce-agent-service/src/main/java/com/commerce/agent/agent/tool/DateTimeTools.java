package com.commerce.agent.agent.tool;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class DateTimeTools {

	@Tool(description = "获取当前服务器时间，适合回答时间、日期、时区相关问题")
	public String getCurrentDateTime() {
		return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
}

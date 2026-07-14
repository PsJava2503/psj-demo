package com.commerce.agent.application.harness;

public enum AgentRunStatus {
	QUEUED, RUNNING, SUCCEEDED, FAILED, CANCELLED, TIMED_OUT;

	public boolean isTerminal() {
		return this == SUCCEEDED || this == FAILED || this == CANCELLED || this == TIMED_OUT;
	}
}

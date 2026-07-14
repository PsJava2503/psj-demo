# commerce-agent-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-agent-service.md` 和 `../docs/agent-memory/AGENT_HARNESS.md`。
- 使用 `$commerce-agent-development`；所有聊天、工作流和 Tool 都必须经过 Harness。
- 保持 Run 唯一终态、预算、事件顺序、Owner 权限、Session 串行和异步身份清理。
- 编码记忆不进入顾客 RAG；新增写 Tool 前设计授权、确认、幂等和审计。
- 运行 `./mvnw -pl commerce-agent-service -am test`。

# commerce-agent-service 服务记忆

## 定位与依赖

- 端口 `8089`，提供商城对话 Agent、业务分析工作流、Run API、SSE 和知识上传。
- 通过 Feign 只读访问 product、order、cart；模型支持 DashScope 和 OpenAI-compatible 配置。
- RAG 默认可使用进程内存，配置后使用 Milvus；当前没有本地业务数据库。

## 当前结构

- `application/harness`：Run 生命周期、事件、预算、并发、超时、取消和 Registry。
- `ChatAgentService`、`SupervisorWorkflowService`、`AgentRoutingService`：模型调用与路由。
- `SessionService`、`UserContextService`：按用户隔离的会话历史、锁和异步身份。
- `CommerceTools`、`DateTimeTools`、`InternalDocsTools`：当前 Tool，全部经过 `HarnessToolExecutor`。
- Knowledge 服务负责上传、分块、Embedding、检索和 Vector Repository 选择。

## 不变量

- 所有聊天和业务工作流都经过 Harness，服从 `AGENT_HARNESS.md` 的状态、预算、事件和隔离规则。
- Session 与 Run 使用“用户 + sessionId/runId”归属；只有 Owner 或管理员可查询/取消。
- 当前业务 Tool 是只读能力；新增写 Tool 前必须引入明确授权、确认、幂等和审计设计。
- 异步线程显式恢复并清理 UserContext；SSE 断开不能让可观测代码破坏 Run。
- 编码 Agent 的 `docs/agent-memory` 不自动进入顾客 RAG；知识上传必须有明确业务用途和权限。
- Run、Session 和默认知识库仍为进程内状态，多实例/重启场景不能宣称持久可靠。

## 修改检查

- 使用 `$commerce-agent-development` 和 `AGENT_HARNESS.md` 的验证矩阵。
- 覆盖身份隔离、Run 终态、预算、Tool 事件、SSE 顺序、取消/超时和 RAG 配置分支。
- 运行 `./mvnw -pl commerce-agent-service -am test`。

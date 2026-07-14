# Agent Harness 记忆

## 定位

`commerce-agent-service` 是面向商城用户的业务 Agent。Harness 是模型和 Tool 外层的确定性运行控制面，负责 Run 生命周期、预算、取消、事件、并发、Session 串行化和归属隔离；模型仍负责自然语言理解与回答。

这些编码记忆文档不会自动进入业务 RAG。业务知识只能通过明确的 knowledge upload/ingestion 流程进入 RAG。

## 当前执行路径

```text
AgentController
  -> AgentHarnessService.run(command)
     -> SessionService: 按 ownerUserId + sessionId 获取会话
     -> AgentRunRegistry: 创建 QUEUED Run
     -> harness executor: 有界并发执行
        -> UserContextService: 在线程内恢复身份
        -> SessionService: 同一用户同一 Session 加锁
        -> AgentRoutingService: CHAT 或 BUSINESS_WORKFLOW
        -> HarnessToolExecutor: Tool 预算与审计事件
        -> ChatAgentService / SupervisorWorkflowService
        -> 写入会话历史并进入终态
```

核心代码位于：

- `application/harness/AgentHarnessService.java`：统一运行入口和超时等待。
- `application/harness/AgentRun.java`：状态、预算、事件和终态原子化。
- `application/harness/AgentRunRegistry.java`：查询、列表、取消、归属和保留数量。
- `application/harness/HarnessToolExecutor.java`：Tool 调用审计与预算。
- `application/service/SessionService.java`：按用户隔离的 Session 与串行锁。
- `interfaces/rest/AgentController.java`：同步、SSE、Run API 和知识上传适配。

## Run 契约

状态：`QUEUED`、`RUNNING`、`SUCCEEDED`、`FAILED`、`CANCELLED`、`TIMED_OUT`。终态只能设置一次，查询结果必须能解释失败原因。

事件序列包括：排队、开始、Session 锁、路由、模型开始/完成、Tool 开始/完成/失败、成功、失败、取消请求、取消、超时。事件具有 `runId`、单 Run 递增 `sequence`、时间和结构化 data。

REST 契约：

- `POST /api/agents/chat`：同步执行，响应包含 `runId` 和 `sessionId`。
- `POST /api/agents/chat_stream`：SSE 输出 status/content/done/error。
- `GET /api/agents/runs/{runId}`：读取自己的 Run；管理员可以读取所有 Run。
- `GET /api/agents/runs?sessionId=&limit=`：按身份过滤后列出 Run。
- `POST /api/agents/runs/{runId}/cancel`：仅所有者或管理员取消。
- `POST /api/agents/business_ops`：兼容旧接口，已废弃，强制业务工作流模式。

## 预算和并发

`agent.harness` 当前配置：

| 配置 | 默认值 | 含义 |
| --- | ---: | --- |
| `max-duration-ms` | 180000 | Run 总时间预算 |
| `max-tool-calls` | 12 | 单 Run Tool 调用上限 |
| `max-output-chars` | 30000 | 模型输出字符预算 |
| `max-events-per-run` | 200 | 单 Run 内存事件保留上限 |
| `max-retained-runs` | 1000 | Registry 内保留 Run 上限 |
| `max-concurrent-runs` | 8 | Harness executor 并发数 |
| `session-lock-timeout-ms` | 5000 | 同 Session 串行锁等待时间 |
| `tool-result-preview-chars` | 500 | 事件内 Tool 结果预览长度 |

新增 Tool 必须通过 `HarnessToolExecutor.execute(...)` 包裹，否则不会受 Tool 预算和事件审计约束。不要把密钥、完整隐私数据或无限长度响应写入事件预览。

## 身份、Session 与线程

- Session storage key 是 `ownerUserId:sessionId`，匿名用户归入 `anonymous`；相同 sessionId 不得跨用户共享历史。
- HTTP 线程解析出的 `UserContext` 必须显式传入异步命令，并在执行线程设置/清理 ThreadLocal。
- 同一用户同一 Session 串行执行，防止历史交错；不同 Session 可并发。
- Run 查询和取消必须经过 Registry 的所有者/管理员校验。
- 新异步入口必须在成功、异常、取消和客户端断开路径清理用户上下文与 executor 资源。

## RAG 边界

- 默认允许上传 `txt`、`md`，通过 `KnowledgeIngestionService` 进入业务知识库。
- 默认内存存储；`agent.rag.use-milvus=true` 时才使用 Milvus。
- 上传、分块、embedding 和检索是知识路径；Run 事件和代码记忆不是 RAG 文档。
- 生产化前仍需补充持久 Session/Run、知识权限、文档版本/删除、敏感信息过滤和可观测存储。

## 修改时的验证矩阵

至少覆盖受影响行：

| 变化 | 必测行为 |
| --- | --- |
| Run 状态 | 成功、模型异常、超时、取消、终态不回退 |
| 预算 | Tool 上限、输出上限、事件截断、耗时上限 |
| Session | 同 Session 串行、不同用户隔离、clear 归属 |
| Tool | started/completed/failed 事件、异常透传、敏感预览 |
| SSE | status/content 顺序、done、error、客户端断开 |
| Run API | 所有者访问、其他用户拒绝、管理员访问、limit 上限 |
| RAG | 扩展名/大小、分块、禁用模式、内存/Milvus 两种配置 |

常用验证：

```bash
./mvnw -pl commerce-agent-service -am test
AGENT_MOCK_ENABLED=true ./mvnw -pl commerce-agent-service spring-boot:run
```

## 已知边界

- Run registry、Session 和默认知识库目前在单进程内存中，多实例之间不共享，重启会丢失。
- `Future.cancel(true)` 依赖下游模型/Tool 正确响应线程中断；外部 I/O 仍需自身超时。
- 当前 Run API 是运行历史观察接口，不是跨实例任务队列或持久化审计系统。
- Harness 预算以字符、次数和墙钟时间为主，尚未实现模型 token/cost 预算。

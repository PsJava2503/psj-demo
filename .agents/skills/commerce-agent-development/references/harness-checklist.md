# Harness 改动检查表

根据改动平面选择对应行，并为每个选中的风险补充测试。

| 区域 | 必查行为 |
| --- | --- |
| Run 创建 | runId 唯一、Owner/Session 已记录、首事件为 `RUN_QUEUED` |
| 生命周期 | queued -> running -> 唯一终态，失败原因可查询 |
| 取消 | Owner/管理员允许、其他用户拒绝、重复取消安全、任务被中断 |
| 超时 | 进入 `TIMED_OUT`、任务取消、迟到成功不能覆盖终态 |
| Tool 预算 | 上限内成功、下一次失败、started/completed/failed 顺序正确 |
| 输出预算 | 同步和流式都计数，部分输出不能报告成功 |
| 事件保留 | sequence 单调递增，达到上限后移除最旧事件 |
| Run 保留 | 只清理符合条件的终态 Run，活动 Run 仍可查询 |
| 并发 | Executor 有界；同 Owner/Session 串行，不同 Session 可并行 |
| 身份 | 异步上下文设置并清理；Session、Run 和业务资源按用户隔离 |
| SSE | status/content 可序列化；唯一 done/error；断开不破坏 Run 记账 |
| 路由 | 显式模式生效；自动模式记录原因；两条路由都经过 Harness |
| Tool 安全 | 输入已校验、读写权限正确、Preview 有界、外部调用有超时 |
| RAG | 扩展名/大小、禁用模式、分块重叠、topK、内存/Milvus 选择 |

## 文件路由

| 改动 | 起点 |
| --- | --- |
| 生命周期/状态/事件 | `application/harness/AgentRun.java`、相关枚举和 record |
| Run 列表/查询/取消 | `AgentRunRegistry.java`、`AgentController.java` |
| 调度/时间预算 | `AgentHarnessService.java`、`AgentProperties.java` |
| Tool 审计/预算 | `HarnessToolExecutor.java`、`agent/tool/*` |
| Session 隔离 | `SessionService.java`、`UserContextService.java` |
| Chat/模型流式 | `ChatAgentService.java`、Provider 适配、`SseMessage.java` |
| 工作流路由 | `AgentRoutingService.java`、`SupervisorWorkflowService.java` |
| 知识/RAG | `KnowledgeIngestionService.java`、RAG 配置和 Vector Repository |

## 冒烟测试提纲

在 mock mode 下：

1. 调用 `POST /api/agents/chat`，保存 `runId` 和 `sessionId`。
2. 同一用户读取 `GET /api/agents/runs/{runId}`，确认 `SUCCEEDED` 和有序事件。
3. 另一用户读取同一 Run，确认不会泄露数据。
4. 调用 `POST /api/agents/chat_stream`，检查 status、content 和终态消息顺序。
5. 测试取消时使用可控的慢速 Fake，不依赖真实供应商偶然变慢。

生命周期测试使用确定性 Fake；真实模型和 Milvus 是集成验证，不能替代 Harness 不变量的单元测试。

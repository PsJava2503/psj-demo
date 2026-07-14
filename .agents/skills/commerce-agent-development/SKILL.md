---
name: commerce-agent-development
description: 实现、调试或审查 commerce-agent-service 的 Agent Harness、Run 生命周期、预算、取消、Tool 执行、用户隔离 Session、SSE、模型路由、业务工作流、知识上传或 RAG。处理 Agent 运行时和 Agent API 时使用；其他电商微服务改动使用 commerce-service-development。
---

# 电商 Agent 开发

在确定性的 Harness 外壳内演进业务 Agent，不削弱生命周期、预算、可观测性、并发和租户隔离保证。

## 1. 加载上下文并分类

1. 读取仓库根 `AGENTS.md`。
2. 读取 `docs/agent-memory/services/commerce-agent-service.md`。
3. 读取 `docs/agent-memory/AGENT_HARNESS.md`。
4. Java、API、持久化或 contract 改动时读取 `docs/agent-memory/ENGINEERING_CONVENTIONS.md`。
5. Tool 读取业务资源或执行业务流程时读取 `docs/agent-memory/BUSINESS_INVARIANTS.md`。
6. 从 `references/harness-checklist.md` 选择受影响的验证项。

编辑前将任务归类：

- 控制面：Run 状态、预算、取消、调度、事件和保留策略。
- 对话面：路由、Prompt/模型调用、Session 历史和 SSE 输出。
- Tool 面：Tool 定义、授权、输入校验、审计和外部 I/O。
- 知识面：上传、解析、分块、Embedding、向量存储和检索。

编码 Agent 记忆保留在 `docs/agent-memory/`，除非用户明确要求，否则不得进入面向商城用户的 RAG。

## 2. 追踪真实执行路径

- 从 `AgentController` 追踪到 `AgentHarnessService`。
- 生命周期或并发变更时检查 `AgentRun`、`AgentRunRegistry`、`HarnessToolExecutor`、`SessionService` 和 `UserContextService`。
- 追踪路由到 `ChatAgentService` 或 `SupervisorWorkflowService`，再检查所有启用的 Tool。
- 新增状态、事件、预算或开关前检查现有测试和配置属性。
- 先检查 `git status --short` 并保留无关改动。

不得为聊天或业务工作流新建绕过 Harness 的第二条执行路径。

## 3. 保持 Harness 不变量

- 每次执行都必须有 `runId`、Owner、Session、有序事件和唯一终态。
- 在确定性边界执行耗时、Tool 次数、输出、事件保留、Run 保留和并发预算。
- 所有 Tool 调用必须经过 `HarnessToolExecutor.execute(...)`。
- Session 历史和锁使用“认证用户 + sessionId”隔离。
- 异步执行显式传递身份，并在所有路径设置和清理线程上下文。
- 只有 Owner 或管理员可以查询和取消 Run。
- 取消和超时必须可观测、幂等，并能处理任务同时完成的竞争。
- 外部模型和 Tool 继续设置自身 I/O 超时，不能只依赖线程中断。

事件和日志不得写入密钥、完整隐私数据或无限长度 Tool 输出。

## 4. 设计流式与失败行为

- SSE status 表达生命周期，content 表达回答分块，流以唯一的 done/error 结果关闭。
- 观察者断开不得破坏 Run 状态；可观测 Consumer 不得让执行失败。
- 同步与流式端点共享路由、Tool、预算、归属和终态语义。
- 失败或不完整回答不得写入 Session 历史。
- 在公共契约允许范围内区分模型、Tool、预算、锁、取消和超时错误。

## 5. 验证

运行：

```bash
./mvnw -pl commerce-agent-service -am test
git diff --check
```

端点或 SSE 变化时使用 mock mode 做 HTTP 冒烟测试。覆盖 `references/harness-checklist.md` 中的相关场景，检查格式化副作用，并报告仍为进程内状态或尚未使用真实模型/向量库验证的部分。

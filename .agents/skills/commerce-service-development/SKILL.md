---
name: commerce-service-development
description: 在本仓库实现或审查 Java/Spring 电商微服务改动，包括 REST API、应用与领域服务、MyBatis 持久化、Flyway migration、Feign contract、事件、Outbox、安全和测试。处理任意 commerce 服务或 commerce-contracts 时使用；commerce-agent-service 的 Harness、Tool、Session、流式输出、模型或 RAG 改动应优先使用 commerce-agent-development。
---

# 电商微服务开发

以最小、可测试的改动维护服务数据所有权、分层依赖、业务状态机和跨服务可靠性。

## 1. 加载最少但充分的上下文

1. 读取仓库根 `AGENTS.md`。
2. 读取 `docs/agent-memory/ENGINEERING_CONVENTIONS.md`。
3. 读取 `docs/agent-memory/services/<目标模块>.md`。
4. 首次进入模块或涉及跨服务改动时，读取 `docs/agent-memory/PROJECT_ARCHITECTURE.md`。
5. 涉及订单、支付、库存、退款、Outbox、身份或授权时，读取 `docs/agent-memory/BUSINESS_INVARIANTS.md`。
6. 使用 `references/change-routing.md` 确认改动归属和受影响文件。

以源码、测试、配置和 Flyway migration 为当前事实。文档与代码不一致时，明确区分“当前实现”和“规划设计”。

## 2. 确定改动边界

- 找到拥有数据和本地事务的服务。
- 从 Controller/Consumer 追踪到 UseCase、Application Service、Domain、Port 和 Infrastructure。
- 命名或新增抽象前，先阅读同一层的 2–3 个相邻文件。
- 先检查 `git status --short`，保留无关的用户改动。
- 编辑前列出受影响的生产者、消费者、contract、migration、权限和状态迁移。

不得通过跨库访问、暴露持久化对象或在 Controller 中堆积业务逻辑来绕过服务边界。

## 3. 按依赖顺序实现

任务涉及对应层时，按以下顺序处理：

1. 定义或调整领域不变量和合法状态迁移。
2. 调整 Domain Repository/Port 接口。
3. 仅在数据确实跨服务时修改共享 contract。
4. 实现 Application 编排和本地事务边界。
5. 实现 MyBatis、Feign、MQ、Outbox 或供应商适配。
6. Schema 变更新增 Flyway migration，不改写已应用历史。
7. 适配 REST/MQ/Scheduler 接口，并校验身份和资源归属。
8. 在改动旁补充测试。

跨服务副作用优先使用“本地状态 + Outbox + 幂等消费者”。若远程调用必须先于本地事务，设计可恢复的孤儿资源回收或补偿路径。

## 4. 保持仓库规范

- 使用构造器注入和目标模块已经采用的 Java 17 写法。
- 协议 DTO 不进入 Domain；Domain 与 `*Data` 的映射放在 Repository 实现。
- `@Transactional` 放在 Application 编排层，不放 Controller 或 Repository。
- 并发状态迁移使用期望旧状态做条件更新，并记录关键状态日志。
- 使用 `SecurityHeaders` 传播认证身份，不得只信任请求体 userId。
- 不做超出任务范围的重构、格式化、依赖升级或端点迁移。

## 5. 按风险验证

运行目标模块及其依赖测试：

```bash
./mvnw -pl <module> -am test
```

共享 contract 或多个服务发生变化时，测试所有消费者或运行 `./mvnw test`。如需运行 Spotless，随后检查并移除无关格式变更。最后执行：

```bash
git diff --check
git status --short
git diff --stat
```

交付时报告行为变化、migration/contract 变化、已运行测试和未验证的外部依赖。

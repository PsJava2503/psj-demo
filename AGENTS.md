# 仓库 Agent 指南

## 范围与事实来源

- 本项目是 Java 17、Maven 多模块电商系统。只在拥有数据或行为的模块内做最小且完整的改动。
- 当前源码、测试、配置和 Flyway migration 是实现事实；文档可能包含规划能力，未经源码确认不得当作现状。
- 先从 `docs/agent-memory/README.md` 选择与任务相关的记忆文档。修改任何模块前，读取 `docs/agent-memory/services/<模块名>.md`。

## 架构规则

- 保持 `interfaces -> application -> domain` 的依赖方向；`infrastructure` 实现 domain/application 定义的端口。Domain 不得依赖 Spring MVC、Feign、MyBatis 或传输 DTO。
- Application Service 负责业务编排和事务边界；Domain Service/Model 负责校验和状态迁移；Infrastructure 负责数据库、RPC、MQ 和供应商适配。
- 跨服务共享 DTO、事件和安全常量放在 `commerce-contracts`。每个服务独占自己的数据库，禁止跨库读写。
- 使用构造器注入。新增抽象前先阅读目标模块同一层的相邻代码并沿用其命名和包结构。
- 客户端 API 使用 `/api/**`；新增仅供服务间调用的端点时使用 `/internal/**`，但不要在无关任务中迁移历史接口。

## 业务与安全规则

- 维护结算单级支付、子订单分摊、库存预占生命周期、条件状态迁移、状态日志和 Outbox 可靠副作用，详见 `docs/agent-memory/BUSINESS_INVARIANTS.md`。
- 使用 `SecurityHeaders` 传播身份；除 Gateway 角色权限外，业务服务还必须校验资源归属。存在认证上下文时，不得只信任请求体中的 userId。
- 消息消费、支付回调、定时 relay 和重试必须幂等。不得把已有 Outbox 可靠流程替换为无保护的单次远程调用。
- 数据库变更只能新增 Flyway migration；不得修改已应用的 migration 历史。

## 改动纪律

- 保留用户已有改动，不做无关清理、依赖升级或全模块格式化。
- 遵守 `.editorconfig`：UTF-8、LF、文件末尾换行、Java 使用宽度 4 的 tab，目标行宽 120。
- pre-commit hook 会执行 `./mvnw -q spotless:apply`。Spotless 可能暴露历史格式差异，运行后必须检查 diff 并移除无关重排。
- 在受影响模块补充测试并运行 `./mvnw -pl <module> -am test`；跨共享 contract 或多个服务时扩大测试范围。
- 交付前运行 `git diff --check`、检查 `git status --short`，并报告已运行和未运行的验证。

## 仓库 Skill 与记忆维护

- Java/Spring 微服务、API、持久化、事件和 migration 任务使用 `$commerce-service-development`。
- `commerce-agent-service` 的 Harness、Tool、Session、SSE、模型路由和 RAG 任务使用 `$commerce-agent-development`。
- 稳定项目事实放在 `docs/agent-memory/`；可复用操作流程放在 `.agents/skills/`。架构或不变量变化时，同步更新对应模块记忆。

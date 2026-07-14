# 工程与代码规范记忆

## 先模仿邻近代码

仓库存在少量历史风格差异。新增代码应先阅读目标模块中同一层的 2–3 个代表文件，沿用其包名、命名、返回类型和异常处理；不要为统一风格扩大任务范围。

## Java 结构与命名

- 使用 Java 17 和构造器注入，不使用字段注入。
- 用 `*Controller` 处理 HTTP，用 `*UseCase` 表达应用能力，用 `*ApplicationService` 编排，用 `*DomainService` 承载业务规则。
- 仓储接口位于 domain；MyBatis 实现和 `*Data` 位于 infrastructure。
- 简单不可变命令、结果、事件和 DTO 优先使用 `record`。
- 查询条件对象可以用 `Optional` 封装缺省值，并提供 `none()`、`hasConditions()` 等语义方法；避免在多层传递散落的 nullable 参数。
- Controller 只做协议工作：校验、身份解析、调用 use case、响应映射。业务分支不要写入 Controller。
- Domain 不引用 Spring MVC、Feign、MyBatis、Servlet 或外部服务 contract。

## 事务、状态与错误

- `@Transactional` 放在 application 的本地业务编排入口；Controller 和 repository 不创建业务事务边界。
- 状态迁移同时校验“当前期望状态”，持久化更新使用条件更新/compare-and-set，避免并发重复推进。
- 重要状态变化写状态日志；跨服务后续动作通过 Outbox 记录在同一事务中。
- 校验失败使用明确、可诊断的业务错误；不要捕获异常后伪造成功。
- 外部回调、消息消费、定时任务和重试必须能安全重复执行。

## API、身份与 Contract

- 外部接口位于 `/api/**`；新建内部调用入口时使用 `/internal/**`。
- Gateway 使用 `CommerceRoles`/`CommercePermissions` 做入口授权，并通过 `SecurityHeaders` 传播身份。
- 服务端必须再次校验资源归属。例如订单、购物车、Session 和 Run 不能仅凭调用者提供的 ID 越权访问。
- 跨服务共享 DTO/事件放 `commerce-contracts`。修改共享 contract 时检查所有生产者、消费者和 Feign client。
- 不把 persistence `*Data`、mapper 类型或服务内部 domain 对象直接暴露为跨服务 contract。

## 数据库与 MyBatis

- 每个服务只修改自己的 migration 和表。
- 使用 `V<n>__description.sql` 追加 Flyway migration；已应用的 migration 视为不可变历史。
- SQL 关键字大写，缩进 4 个空格。
- repository 实现负责 Domain 与 `*Data` 的双向映射；Dynamic SQL mapper 只做数据访问。
- schema 变更要同步检查 mapper、result mapping、repository mapping、测试数据和部署配置。

## 格式化与文件风格

`.editorconfig` 是基础约定：

- UTF-8、LF、文件末尾换行、移除行尾空格。
- Java/XML 使用宽度 4 的 tab；Java 目标行宽 120。
- YAML 使用 2 个空格；Shell 使用 2 个空格；SQL 使用 4 个空格。

Git pre-commit hook 执行 `./mvnw -q spotless:apply`。Spotless 使用 Eclipse formatter 并移除未使用 import，但现存文件并非全部处于统一格式。运行后必须检查 diff；只保留任务范围内的格式变化，不提交无关的全模块重排。

## 测试与交付

- 新增领域规则：测试合法路径、非法前置状态、边界值和重复调用。
- 新增 repository/schema：覆盖映射或集成路径，至少验证 migration 与 mapper 能编译。
- 新增 API：覆盖校验、身份/归属、成功和失败响应。
- 新增异步/outbox：覆盖幂等、重试、终态和故障路径。
- 新增 Agent Harness 行为：参见 `AGENT_HARNESS.md` 的验证矩阵。

常用命令：

```bash
./mvnw -pl commerce-order-service -am test
./mvnw -pl commerce-agent-service -am test
./mvnw test
git diff --check
git status --short
```

按影响范围选择最小但充分的测试。跨 `commerce-contracts` 或多个服务的变更应扩大到所有受影响模块或全仓测试。

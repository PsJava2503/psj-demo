# 项目架构记忆

## 技术与部署形态

- Java 17、Spring Boot、Spring Cloud、Maven Wrapper 多模块工程。
- Nacos 提供服务发现；服务间调用主要使用 OpenFeign。
- MyBatis Dynamic SQL 负责持久化；Flyway 管理各服务独立数据库的 schema 历史。
- Gateway 负责入口认证与角色权限，业务服务继续负责资源归属和领域校验。
- 可靠的跨服务副作用优先使用 Outbox、定时 relay、重试和幂等消费。

## 模块地图

| 模块 | 责任 | 默认端口 |
| --- | --- | ---: |
| `commerce-gateway` | 外部入口、认证、RBAC、身份头转发 | 8080 |
| `commerce-order-service` | 结算单、子订单、订单项、状态日志、库存关联、退款请求 | 8081 |
| `commerce-inventory-service` | 库存、预占、确认和释放 | 8082 |
| `commerce-user-service` | 用户、认证、角色和权限 | 8083 |
| `commerce-product-service` | 商品和商品查询 | 8084 |
| `commerce-payment-service` | 支付单、支付分摊、回调、退款和退款分摊 | 8085 |
| `commerce-notification-service` | 通知发送与记录 | 8086 |
| `commerce-address-service` | 用户地址 | 8087 |
| `commerce-cart-service` | 用户购物车 | 8088 |
| `commerce-agent-service` | 商城 Agent、Harness、Tool、Session、RAG | 8089 |
| `commerce-contracts` | 跨服务共享 DTO、Feign contract、安全常量 | 不运行 |

`psj-commerce-*` 目录中的 `target/` 等内容不是新功能开发入口；开始改动前以根 `pom.xml` 的 modules 和当前源码目录为准。

## 服务内分层

典型调用方向：

```text
HTTP / MQ / Scheduler
        |
        v
interfaces (REST DTO, controller, consumer)
        |
        v
application (use case, orchestration, transaction)
        |
        v
domain (model, validation, transition, repository port)
        ^
        |
infrastructure (MyBatis, Feign, MQ, provider adapter)
```

- `interfaces/rest`：协议适配、参数校验和响应映射；不承载业务事务。
- `application/port`：对外暴露用例接口；常以 `*UseCase` 命名。
- `application/service`：编排多个领域/端口，放事务边界和跨聚合流程。
- `domain/model`、`domain/service`：领域状态、校验和合法迁移，不依赖 Web 或数据库框架。
- `domain/port` 或 `domain/repository`：领域所需能力的接口。
- `infrastructure/persistence|rpc|mq`：实现端口，完成 Domain 与 `*Data`/外部 contract 的映射。

依赖可以从外层指向内层；不要让 domain 反向依赖 controller、Feign client、mapper 或传输 DTO。

## 跨模块边界

- 每个服务只拥有自己的数据库。跨服务查询通过 contract/API/event 完成。
- 多个服务共同使用的 DTO、事件和安全常量放到 `commerce-contracts`；单服务私有 DTO 留在服务内。
- `/api/**` 是客户端 API。新增仅供服务间调用的入口时使用 `/internal/**` 并校验调用身份。
- 部分历史 Feign client 仍调用 `/api/**`；这是现状，不应在无关任务中批量迁移。
- 强一致性只在单服务本地事务内成立。跨服务一致性通过预占、状态机、Outbox、重试、对账和补偿获得。

## 判断“现状”与“规划”

`COMMERCE_BUSINESS_FLOW.md` 包含有价值的业务设计，也包含 MES、物流等尚未完整落地的方向。回答“当前系统怎么工作”或修改代码时：

1. 先查当前源码、配置、测试和 migration。
2. 再使用 README 和业务流程文档补充意图。
3. 若两者不一致，明确写“当前实现”和“规划设计”，不要静默选择规划版本。

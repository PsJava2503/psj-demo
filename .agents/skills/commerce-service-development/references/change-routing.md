# 改动路由参考

先定位拥有该能力的模块，再按目标模块现有包结构选择文件。

| 需求类型 | 主要位置 | 同时检查 |
| --- | --- | --- |
| HTTP 请求/响应 | `interfaces/rest`、REST DTO | UseCase、参数校验、Gateway 权限 |
| 业务流程 | `application/service`、`application/port` | Domain 规则、事务、远程 Port |
| 校验/状态迁移 | `domain/service`、`domain/model` | 条件更新、状态日志、测试 |
| 查询/持久化 | Domain Repository + `infrastructure/persistence` | `*Data`、Dynamic SQL Mapper、映射 |
| Schema/索引/字段 | 服务的 `db/migration` | Mapper、Repository 映射、测试数据 |
| 跨服务 DTO/事件 | `commerce-contracts` | 所有生产者、消费者、Feign Client |
| 服务间调用 | Infrastructure RPC/Feign Adapter | 超时、身份头、失败与补偿 |
| 可靠副作用 | 本地 Outbox + Relay + Consumer | 幂等键、重试、终态失败 |
| 授权 | Gateway 权限 + 资源所属服务 | Role、Permission、资源归属 |
| 定时对账/补偿 | Scheduler/Application | 分页、幂等、锁、可观测性 |

## 模块记忆路由

修改模块前读取 `docs/agent-memory/services/` 下的同名文件：

- `commerce-gateway`：入口路由、Sa-Token 校验、权限和身份头。
- `commerce-contracts`：跨服务 DTO、事件、MQ 拓扑和安全常量。
- `commerce-user-service`：认证、Sa-Token Session、RBAC、用户与凭证。
- `commerce-product-service`：商品目录、启用/软删除和内部商品快照。
- `commerce-address-service`：地址归属、默认地址唯一性和软删除。
- `commerce-cart-service`：购物车合并、数量语义、商品快照和用户隔离。
- `commerce-order-service`：结算单、子订单、库存预占、状态机、Outbox、GraphQL。
- `commerce-payment-service`：单笔真实支付、分摊、回调、退款、对账、Outbox。
- `commerce-inventory-service`：仓/库位余额、预占、确认/释放、流水、收货单。
- `commerce-notification-service`：LOG 通道、订单通知和幂等记录。
- `commerce-agent-service`：使用 `$commerce-agent-development` 及 Harness 专属记忆。

## 高风险检查

### 订单、库存、支付和退款

- 保持结算单级真实支付与子订单分摊分离。
- 保持预占、绑定、确认、释放、过期和孤儿回收一致。
- 用期望旧状态拒绝无效或过期的状态推进。
- 记录状态历史，并通过 Outbox 可靠发送后续动作。
- 对账金额总和并明确舍入规则。

### 安全与身份

- 校验 Gateway 的 Role/Permission 规则。
- 校验 Feign 和异步执行的 `SecurityHeaders` 传播。
- 校验资源所属服务的 Owner 检查。
- 测试用户间、商家间隔离，而不只测试未登录。

### Contract 与数据库

- 全仓搜索变更类型、序列化字段和消费者。
- 独立部署的生产者与消费者之间保持兼容，优先新增字段而非改变旧字段语义。
- 新增更高版本的 `V<n>__description.sql`，回填需兼容历史数据量。
- 索引必须对应实际查询路径，并评估写入成本和唯一性语义。

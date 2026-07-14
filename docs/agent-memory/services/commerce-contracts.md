# commerce-contracts 模块记忆

## 定位与边界

- 这是不运行的共享 JAR，没有端口、数据库和业务事务。
- 只放真正跨服务的稳定协议：REST DTO、支付/退款分摊请求、MQ 事件与拓扑、安全常量、通用 MyBatis 类型处理器。
- 不放任何单服务私有的 Domain Model、Persistence Data、Repository 或业务编排。

## 当前内容

- `address`、`cart`、`product`、`user`：跨服务响应 record。
- `payment`：支付预创建、支付分摊、退款和支付汇总 contract。
- `messaging`：`OrderCreatedEvent`、`PaymentPaidEvent` 和 `MqTopology`。
- `security`：`CommerceRoles`、`CommercePermissions`、`SecurityHeaders`、`AuthSession`。
- `mybatis`：PostgreSQL `TIMESTAMPTZ` 与 `ZonedDateTime` 的类型处理器。

## 不变量

- Contract 变更要考虑生产者与消费者独立部署，优先做可选/向后兼容的新增。
- 不改变已有字段含义，不用同一个事件名承载不兼容的新语义。
- 金额继续使用 `BigDecimal`；ID 类型和 JSON 字段名保持稳定。
- 集合字段返回值要有清晰的 null/空列表语义，支付 contract 当前通过访问器归一为空列表。
- 安全常量变化必须同步 user-service 的 seed migration、Gateway 权限映射和所有业务服务。

## 修改检查

- 全仓搜索类型名、构造位置、反序列化位置和 Feign 方法。
- MQ 事件变化检查 Exchange、Queue、Routing Key、Publisher 和 Listener。
- 运行 `./mvnw -pl commerce-contracts -am test`，并编译/测试所有受影响消费者。

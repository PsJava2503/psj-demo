# commerce-order-service 服务记忆

## 定位与依赖

- 端口 `8081`，拥有 `commerce_order` PostgreSQL 数据库，使用 RabbitMQ、Nacos 和 OpenFeign。
- 同步依赖 user、address、product、inventory、payment、notification 服务。
- `/api/orders` 提供创建、查询、取消、退款、发货、收货和完成；GraphQL 位于 `/api/orders/graphql`。
- 消费 `PaymentPaidEvent`；`OrderOutboxRelay` 可靠推进库存、支付、通知和订单事件。

## 当前数据模型

- `orders`：结算单；`order_sub_orders`：子订单；`order_items`：成交商品快照。
- `order_inventory_reservations`：库存预占关联；`order_status_logs`：状态历史。
- `order_refund_requests`：订单侧退款业务请求；`order_outbox_events`：可靠副作用。

## 核心流程

- 创建时查询用户、地址和商品快照，生成订单 ID 后先预占库存。
- 预占成功后本地事务写订单、子订单、订单项、预占关联和状态日志，再绑定库存预占。
- 创建一笔结算单级支付，并按子订单生成 allocation；失败通过 Outbox 释放库存。
- 支付成功事件推进订单并由 Outbox 确认库存、通知；取消/超时写释放库存、关闭支付和通知事件。
- 退款先写订单侧请求和 Outbox，再由 payment-service 执行真实退款及分摊。

## 不变量

- 服从 `BUSINESS_INVARIANTS.md` 的支付、库存、状态机、Outbox 和资源归属规则。
- 状态更新带期望旧状态并检查行数；重要迁移写 `order_status_logs`。
- 远程调用失败不能留下不可恢复状态；库存孤儿由 TTL 回收，其他副作用由 Outbox 重试。
- 查询、取消、退款和履约操作必须校验当前用户/商家对订单的访问权；不能只凭 orderId。
- GraphQL 只聚合读取，不创建新的跨服务事务或绕过 REST/Application 授权。

## 修改检查

- 覆盖库存不足、绑定失败、支付预创建失败、重复支付事件、取消竞争、超时和退款分摊。
- Contract 变化检查六个 Feign Port、MQ Producer/Consumer 和 GraphQL 映射。
- 运行 `./mvnw -pl commerce-order-service -am test`。

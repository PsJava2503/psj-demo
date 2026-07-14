# commerce-payment-service 服务记忆

## 定位与依赖

- 端口 `8085`，拥有 `commerce_payment` PostgreSQL 数据库，对接支付宝沙箱并通过 Nacos 注册。
- `/api/payments` 提供用户支付操作、支付宝 notify、退款、关闭、查询和对账。
- `/internal/payments` 为 order-service 提供预创建、详细分摊、汇总、关闭和退款。
- 定时任务主动查询待支付订单并关闭超时支付；Outbox 发布支付成功事件。

## 当前数据模型

- `payment_order`：每个 checkoutOrderId 唯一的一笔真实支付。
- `payment_allocation`：支付到子订单/商家的分摊。
- `payment_notify_log`、`idempotency_record`：回调审计和去重。
- `payment_refund`、`payment_refund_allocation`：退款及分摊。
- `payment_outbox_event`：可靠支付事件；reconcile 表记录账单、明细和差异。

## 不变量

- 同一结算单只能有一笔真实渠道支付；allocation 的 paidAmount 总和等于支付金额。
- refund allocation 总和等于退款金额，累计退款不能超过原支付/分摊金额。
- notify 是匿名入口，但必须先验签、记录原始回调、幂等去重，再推进状态。
- `TRADE_SUCCESS` 等终态不得被较旧的 query/notify 回退；重复回调不能重复发事件或退款。
- 支付成功事件先写本地 Outbox，再由 Relay 发布；不能只依赖一次 RabbitMQ 调用。
- 支付密钥仅来自环境变量/安全配置，禁止写入仓库、日志和 Run 事件。

## 修改检查

- 覆盖验签失败、重复 notify、乱序状态、allocation 总和、重复退款、部分退款、超时关闭和对账差异。
- 金额统一使用 `BigDecimal.compareTo` 和明确精度，不使用 double。
- 运行 `./mvnw -pl commerce-payment-service -am test`。

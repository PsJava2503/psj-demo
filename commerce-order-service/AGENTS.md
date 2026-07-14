# commerce-order-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-order-service.md` 和 `../docs/agent-memory/BUSINESS_INVARIANTS.md`。
- 保持“先库存预占、再本地事务、提交后绑定、结算单级支付、子订单分摊”的流程。
- 状态迁移使用期望旧状态并记录日志；跨服务副作用写 Outbox 并保持消费者幂等。
- 所有订单查询、取消、退款和履约操作校验用户/商家资源归属。
- 运行 `./mvnw -pl commerce-order-service -am test`。

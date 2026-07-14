# commerce-payment-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-payment-service.md` 和 `../docs/agent-memory/BUSINESS_INVARIANTS.md`。
- 一个 checkout order 只有一笔真实支付；支付和退款 allocation 总和必须对账。
- 支付回调先验签、记录、幂等去重，再推进不可回退状态并写 Outbox。
- 密钥只来自安全环境配置，金额使用 `BigDecimal`，不得泄露原始敏感参数。
- 运行 `./mvnw -pl commerce-payment-service -am test`。

# commerce-cart-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-cart-service.md`。
- 所有购物车查询和写入绑定认证用户，不能只信任参数或请求体 userId。
- 当前活动条目按 userId + productId 合并；改变合并键必须同步索引、Domain 和 migration。
- 购物车价格是快照，结算仍由订单服务重新读取当前商品事实。
- 运行 `./mvnw -pl commerce-cart-service -am test`。

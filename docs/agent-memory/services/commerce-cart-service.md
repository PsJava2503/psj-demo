# commerce-cart-service 服务记忆

## 定位与接口

- 端口 `8088`，拥有 `commerce_cart` PostgreSQL 数据库和 `cart_items` 表。
- `/api/carts` 提供添加、改数量、删除单项、清空、条件查询和详情。
- Agent 通过 Feign 读取购物车；当前没有独立 `/internal/**` 购物车接口。

## 当前行为

- 活动购物车使用“userId + productId + 未删除”部分唯一索引；当前合并键不包含 skuId。
- 再次添加同一用户/商品会累加数量、更新 sku/name/price 快照，并设为 selected。
- 新条目默认 selected=true、deleted=false。
- 数量更新为 0 或负数时转为删除语义。
- 商品名称和单价是加入购物车时的快照，结算仍应重新查询 product-service 的当前价格和可用状态。

## 不变量

- 查询、更新、删除和清空必须绑定认证用户；当前 API 接收 userId 参数/请求体，新增改动应改用或校验 `SecurityHeaders`，不能扩大越权面。
- 若改为按 SKU 合并，必须同步唯一索引、Domain 查询条件、API 语义和历史数据 migration。
- 删除语义与唯一部分索引保持一致，已删除条目不得阻止重新添加。
- 购物车不是成交价格事实，订单服务仍是结算时价格快照的创建者。

## 修改检查

- 覆盖重复添加、不同用户隔离、不同 SKU、数量归零、删除后重加和软删除过滤。
- `CartItemResponse` 变化时编译 agent-service。
- 运行 `./mvnw -pl commerce-cart-service -am test`。

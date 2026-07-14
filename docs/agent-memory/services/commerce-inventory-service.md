# commerce-inventory-service 服务记忆

## 定位与接口

- 端口 `8082`，拥有 `commerce_inventory` PostgreSQL 数据库。
- `/api/inventory` 提供仓库、库位、入库、出库、调整、锁定、余额和收货单能力。
- `/internal/inventory` 为订单服务提供 reserve、bind、release、confirm 等库存流程。

## 当前模型

- `inv_warehouses`、`inv_bins`：仓和库位；库位在仓内位置唯一。
- `inv_balances`、`inv_bin_balances`：仓级和库位级数量/锁定量。
- `inv_reservations`：预占、剩余量、取消时间、有效期和 `bound_order`。
- `inv_transactions`、warehouse/bin ledgers：每次库存变化的事务和双层流水。
- `inv_receipts`、records、sequence_numbers：收货单、明细和编号。

## 核心不变量

- 预占可跨多个库位拆分；只有全部数量可锁定时才返回 reservation IDs。
- 新预占先为未绑定并带孤儿 TTL；订单本地提交后 bind，定时任务释放过期未绑定预占。
- confirm 消耗预占并实际出库；release 解除锁定。重复或冲突操作必须通过 reservation 状态/版本检查安全拒绝或幂等结束。
- 仓级与库位级余额、锁定量和流水必须在同一事务保持一致，不能出现负可用量。
- 库位内调整要求同仓；跨仓调拨要维护在途量和相应流水。
- 商品服务不拥有库存数量，连接键为 skuId。

## 修改检查

- 覆盖不足库存、跨库位拆分、并发预占、绑定、TTL 回收、重复确认/释放和版本冲突。
- 新库存动作同时检查余额 Delta、Reservation、Transaction、两级 Ledger 和收货单影响。
- 公共库存写 API 需要 `inventory:update`，只读使用 `inventory:view`。
- 运行 `./mvnw -pl commerce-inventory-service -am test`。

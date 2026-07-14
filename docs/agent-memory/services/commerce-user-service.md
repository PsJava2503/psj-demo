# commerce-user-service 服务记忆

## 定位与依赖

- 端口 `8083`，拥有 `commerce_user` PostgreSQL 数据库，使用 Redis/Sa-Token 保存登录会话并通过 Nacos 注册。
- 拥有用户、凭证、角色、权限、用户角色、角色权限和用户地址槽位数据。
- 对外提供 `/api/auth/**`、`/api/users/**`、`/api/rbac/**`；对订单服务提供 `/internal/users/{userId}`。

## 当前认证与 RBAC

- 注册校验基本字段，创建用户和 salted SHA-256 凭证，默认分配 `CUSTOMER` 角色，然后自动登录。
- 登录校验凭证与用户启用状态，把 userId、username、roles、permissions 快照写入 Sa-Token token session。
- 修改密码后注销该用户会话；禁用登录会删除/禁用凭证路径并注销会话。
- `StpInterfaceImpl` 从 `RbacRepository` 读取角色和权限。
- Migration 初始化 `ADMIN`、`CUSTOMER`、`MERCHANT` 及基础 permission；购物车和 Agent permission 由后续 migration 增加。

## 不变量

- phone、email、username 和 role/permission code 保持唯一。
- 凭证数据不得出现在 REST response、日志、共享 contract 或 Agent Tool 输出中。
- 新 permission 同时修改 `CommercePermissions`、新增 Flyway seed migration、角色授权和 Gateway 映射。
- 改变用户角色/权限后要考虑已登录 token session 中的权限快照何时刷新或失效。
- 密码算法升级要使用兼容迁移/登录升级策略，不得直接让历史凭证失效；当前 SHA-256 方案不应被描述为生产级密码存储。
- `/internal/users/{userId}` 只返回共享 `UserResponse`，不要泄露凭证。

## 修改检查

- 覆盖注册重复字段、登录失败、禁用用户、改密后会话失效、角色/权限组合和软删除。
- RBAC 管理 API 必须有明确管理员授权，不能只依赖“已登录”。
- 运行 `./mvnw -pl commerce-user-service -am test`。

# commerce-user-service 模块规则

- 修改前读取 `../docs/agent-memory/services/commerce-user-service.md` 和业务安全相关记忆。
- 保护凭证、Sa-Token Session、RBAC 唯一性和已登录权限快照语义。
- 新权限同步 `commerce-contracts`、新增 seed migration、角色授权和 Gateway 映射。
- 认证/RBAC 管理 API 必须有明确授权，并测试会话失效和越权路径。
- 运行 `./mvnw -pl commerce-user-service -am test`。

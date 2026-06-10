package com.commerce.user.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class RolePermissionDynamicSqlSupport {

	public static final RolePermissionTable rolePermissions = new RolePermissionTable();
	public static final SqlColumn<Long> roleId = rolePermissions.roleId;
	public static final SqlColumn<Long> permissionId = rolePermissions.permissionId;
	public static final SqlColumn<ZonedDateTime> assignedAt = rolePermissions.assignedAt;
	public static final SqlColumn<Long> assignedBy = rolePermissions.assignedBy;

	private RolePermissionDynamicSqlSupport() {
	}

	public static final class RolePermissionTable extends SqlTable {

		public final SqlColumn<Long> roleId = column("role_id");
		public final SqlColumn<Long> permissionId = column("permission_id");
		public final SqlColumn<ZonedDateTime> assignedAt = column("assigned_at");
		public final SqlColumn<Long> assignedBy = column("assigned_by");

		public RolePermissionTable() {
			super("role_permissions");
		}
	}

}

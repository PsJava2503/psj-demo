package com.commerce.user.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserRoleDynamicSqlSupport {

	public static final UserRoleTable userRoles = new UserRoleTable();
	public static final SqlColumn<Long> userId = userRoles.userId;
	public static final SqlColumn<Long> roleId = userRoles.roleId;
	public static final SqlColumn<ZonedDateTime> assignedAt = userRoles.assignedAt;
	public static final SqlColumn<Long> assignedBy = userRoles.assignedBy;

	private UserRoleDynamicSqlSupport() {
	}

	public static final class UserRoleTable extends SqlTable {

		public final SqlColumn<Long> userId = column("user_id");
		public final SqlColumn<Long> roleId = column("role_id");
		public final SqlColumn<ZonedDateTime> assignedAt = column("assigned_at");
		public final SqlColumn<Long> assignedBy = column("assigned_by");

		public UserRoleTable() {
			super("user_roles");
		}
	}

}

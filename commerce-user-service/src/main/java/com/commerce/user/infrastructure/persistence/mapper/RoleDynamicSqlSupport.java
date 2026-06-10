package com.commerce.user.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class RoleDynamicSqlSupport {

	public static final RoleTable roles = new RoleTable();
	public static final SqlColumn<Long> id = roles.id;
	public static final SqlColumn<String> code = roles.code;
	public static final SqlColumn<String> name = roles.name;
	public static final SqlColumn<String> description = roles.description;
	public static final SqlColumn<Boolean> enabled = roles.enabled;
	public static final SqlColumn<Boolean> deleted = roles.deleted;
	public static final SqlColumn<ZonedDateTime> createTime = roles.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = roles.updateTime;

	private RoleDynamicSqlSupport() {
	}

	public static final class RoleTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> code = column("code");
		public final SqlColumn<String> name = column("name");
		public final SqlColumn<String> description = column("description");
		public final SqlColumn<Boolean> enabled = column("enabled");
		public final SqlColumn<Boolean> deleted = column("deleted");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public RoleTable() {
			super("roles");
		}
	}

}

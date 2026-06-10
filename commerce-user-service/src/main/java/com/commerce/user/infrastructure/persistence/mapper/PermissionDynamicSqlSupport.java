package com.commerce.user.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PermissionDynamicSqlSupport {

	public static final PermissionTable permissions = new PermissionTable();
	public static final SqlColumn<Long> id = permissions.id;
	public static final SqlColumn<String> code = permissions.code;
	public static final SqlColumn<String> resource = permissions.resource;
	public static final SqlColumn<String> action = permissions.action;
	public static final SqlColumn<String> name = permissions.name;
	public static final SqlColumn<String> description = permissions.description;
	public static final SqlColumn<Boolean> enabled = permissions.enabled;
	public static final SqlColumn<Boolean> deleted = permissions.deleted;
	public static final SqlColumn<ZonedDateTime> createTime = permissions.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = permissions.updateTime;

	private PermissionDynamicSqlSupport() {
	}

	public static final class PermissionTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> code = column("code");
		public final SqlColumn<String> resource = column("resource");
		public final SqlColumn<String> action = column("action");
		public final SqlColumn<String> name = column("name");
		public final SqlColumn<String> description = column("description");
		public final SqlColumn<Boolean> enabled = column("enabled");
		public final SqlColumn<Boolean> deleted = column("deleted");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public PermissionTable() {
			super("permissions");
		}
	}

}

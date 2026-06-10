package com.commerce.user.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserDynamicSqlSupport {

	public static final UserTable users = new UserTable();
	public static final SqlColumn<Long> id = users.id;
	public static final SqlColumn<String> firstName = users.firstName;
	public static final SqlColumn<String> secondName = users.secondName;
	public static final SqlColumn<String> phone = users.phone;
	public static final SqlColumn<String> email = users.email;
	public static final SqlColumn<Long> defaultAddressSlotId = users.defaultAddressSlotId;
	public static final SqlColumn<Boolean> enabled = users.enabled;
	public static final SqlColumn<Boolean> deleted = users.deleted;
	public static final SqlColumn<ZonedDateTime> createTime = users.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = users.updateTime;

	private UserDynamicSqlSupport() {
	}

	public static final class UserTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> firstName = column("first_name");
		public final SqlColumn<String> secondName = column("second_name");
		public final SqlColumn<String> phone = column("phone");
		public final SqlColumn<String> email = column("email");
		public final SqlColumn<Long> defaultAddressSlotId = column("default_address_slot_id");
		public final SqlColumn<Boolean> enabled = column("enabled");
		public final SqlColumn<Boolean> deleted = column("deleted");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public UserTable() {
			super("users");
		}
	}

}

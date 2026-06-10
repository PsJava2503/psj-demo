package com.commerce.user.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserCredentialDynamicSqlSupport {

	public static final UserCredentialTable userCredentials = new UserCredentialTable();
	public static final SqlColumn<Long> userId = userCredentials.userId;
	public static final SqlColumn<String> username = userCredentials.username;
	public static final SqlColumn<String> passwordHash = userCredentials.passwordHash;
	public static final SqlColumn<String> passwordSalt = userCredentials.passwordSalt;
	public static final SqlColumn<String> passwordAlgorithm = userCredentials.passwordAlgorithm;
	public static final SqlColumn<Boolean> enabled = userCredentials.enabled;
	public static final SqlColumn<ZonedDateTime> createTime = userCredentials.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = userCredentials.updateTime;

	private UserCredentialDynamicSqlSupport() {
	}

	public static final class UserCredentialTable extends SqlTable {

		public final SqlColumn<Long> userId = column("user_id");
		public final SqlColumn<String> username = column("username");
		public final SqlColumn<String> passwordHash = column("password_hash");
		public final SqlColumn<String> passwordSalt = column("password_salt");
		public final SqlColumn<String> passwordAlgorithm = column("password_algorithm");
		public final SqlColumn<Boolean> enabled = column("enabled");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public UserCredentialTable() {
			super("user_credentials");
		}
	}

}

package com.commerce.address.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class AddressDynamicSqlSupport {

	public static final AddressTable addresses = new AddressTable();
	public static final SqlColumn<Long> id = addresses.id;
	public static final SqlColumn<Long> userId = addresses.userId;
	public static final SqlColumn<String> recipientName = addresses.recipientName;
	public static final SqlColumn<String> phone = addresses.phone;
	public static final SqlColumn<String> province = addresses.province;
	public static final SqlColumn<String> city = addresses.city;
	public static final SqlColumn<String> district = addresses.district;
	public static final SqlColumn<String> detail = addresses.detail;
	public static final SqlColumn<Boolean> defaultAddress = addresses.defaultAddress;
	public static final SqlColumn<Boolean> deleted = addresses.deleted;
	public static final SqlColumn<ZonedDateTime> createTime = addresses.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = addresses.updateTime;

	private AddressDynamicSqlSupport() {
	}

	public static final class AddressTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> userId = column("user_id");
		public final SqlColumn<String> recipientName = column("recipient_name");
		public final SqlColumn<String> phone = column("phone");
		public final SqlColumn<String> province = column("province");
		public final SqlColumn<String> city = column("city");
		public final SqlColumn<String> district = column("district");
		public final SqlColumn<String> detail = column("detail");
		public final SqlColumn<Boolean> defaultAddress = column("is_default");
		public final SqlColumn<Boolean> deleted = column("deleted");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public AddressTable() {
			super("addresses");
		}
	}
}

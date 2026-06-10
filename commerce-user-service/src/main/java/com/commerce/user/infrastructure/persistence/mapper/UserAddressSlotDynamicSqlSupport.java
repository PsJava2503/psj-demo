package com.commerce.user.infrastructure.persistence.mapper;

import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserAddressSlotDynamicSqlSupport {

	public static final UserAddressSlotTable userAddressSlots = new UserAddressSlotTable();
	public static final SqlColumn<Long> id = userAddressSlots.id;
	public static final SqlColumn<Long> userId = userAddressSlots.userId;
	public static final SqlColumn<Long> addressId = userAddressSlots.addressId;
	public static final SqlColumn<String> slotName = userAddressSlots.slotName;
	public static final SqlColumn<Boolean> deleted = userAddressSlots.deleted;
	public static final SqlColumn<ZonedDateTime> createTime = userAddressSlots.createTime;

	private UserAddressSlotDynamicSqlSupport() {
	}

	public static final class UserAddressSlotTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<Long> userId = column("user_id");
		public final SqlColumn<Long> addressId = column("address_id");
		public final SqlColumn<String> slotName = column("slot_name");
		public final SqlColumn<Boolean> deleted = column("deleted");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");

		public UserAddressSlotTable() {
			super("user_address_slots");
		}
	}

}

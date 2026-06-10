package com.commerce.user.infrastructure.persistence.mapper;

import static com.commerce.user.infrastructure.persistence.mapper.UserAddressSlotDynamicSqlSupport.addressId;
import static com.commerce.user.infrastructure.persistence.mapper.UserAddressSlotDynamicSqlSupport.createTime;
import static com.commerce.user.infrastructure.persistence.mapper.UserAddressSlotDynamicSqlSupport.deleted;
import static com.commerce.user.infrastructure.persistence.mapper.UserAddressSlotDynamicSqlSupport.id;
import static com.commerce.user.infrastructure.persistence.mapper.UserAddressSlotDynamicSqlSupport.slotName;
import static com.commerce.user.infrastructure.persistence.mapper.UserAddressSlotDynamicSqlSupport.userAddressSlots;
import static com.commerce.user.infrastructure.persistence.mapper.UserAddressSlotDynamicSqlSupport.userId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.commerce.user.infrastructure.persistence.model.UserAddressSlotData;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface UserAddressSlotDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(id, userId, addressId, slotName, deleted, createTime);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "UserAddressSlotDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT),
			@Result(column = "address_id", property = "addressId", jdbcType = JdbcType.BIGINT),
			@Result(column = "slot_name", property = "slotName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "deleted", property = "deleted", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<UserAddressSlotData> selectMany(SelectStatementProvider selectStatement);

	default List<UserAddressSlotData> selectByUserId(Long userIdValue) {
		return MyBatis3Utils.selectList(this::selectMany, selectList, userAddressSlots, c -> c
				.where(userId, isEqualTo(userIdValue))
				.and(deleted, isEqualTo(false))
				.orderBy(id)
		);
	}

}

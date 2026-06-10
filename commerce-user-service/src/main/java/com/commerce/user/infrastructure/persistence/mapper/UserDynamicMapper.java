package com.commerce.user.infrastructure.persistence.mapper;

import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.createTime;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.defaultAddressSlotId;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.deleted;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.email;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.enabled;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.firstName;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.id;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.phone;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.secondName;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.updateTime;
import static com.commerce.user.infrastructure.persistence.mapper.UserDynamicSqlSupport.users;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.user.domain.model.UserQueryOptions;
import com.commerce.user.infrastructure.persistence.model.UserData;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface UserDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, firstName, secondName, phone, email, defaultAddressSlotId, enabled, deleted, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<UserData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "UserDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "second_name", property = "secondName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "phone", property = "phone", jdbcType = JdbcType.VARCHAR),
			@Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
			@Result(column = "default_address_slot_id", property = "defaultAddressSlotId", jdbcType = JdbcType.BIGINT),
			@Result(column = "enabled", property = "enabled", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "deleted", property = "deleted", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<UserData> selectMany(SelectStatementProvider selectStatement);

	default int create(UserData userData) {
		return MyBatis3Utils.insert(this::insert, userData, users, c -> c
				.map(firstName).toProperty("firstName")
				.map(secondName).toProperty("secondName")
				.map(phone).toProperty("phone")
				.map(email).toProperty("email")
				.map(defaultAddressSlotId).toPropertyWhenPresent("defaultAddressSlotId", userData::defaultAddressSlotId)
				.map(enabled).toPropertyWhenPresent("enabled", userData::enabled)
				.map(deleted).toPropertyWhenPresent("deleted", userData::deleted)
				.map(createTime).toPropertyWhenPresent("createTime", userData::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", userData::updateTime)
		);
	}

	default int update(UserData userData) {
		return MyBatis3Utils.update(this::updateStatement, users, c -> c
				.set(firstName).equalToWhenPresent(userData::firstName)
				.set(secondName).equalToWhenPresent(userData::secondName)
				.set(phone).equalToWhenPresent(userData::phone)
				.set(email).equalToWhenPresent(userData::email)
				.set(defaultAddressSlotId).equalToWhenPresent(userData::defaultAddressSlotId)
				.set(enabled).equalToWhenPresent(userData::enabled)
				.set(deleted).equalToWhenPresent(userData::deleted)
				.set(createTime).equalToWhenPresent(userData::createTime)
				.set(updateTime).equalToWhenPresent(userData::updateTime)
				.where(id, isEqualTo(userData::id))
		);
	}

	default int delete(Long idValue) {
		return MyBatis3Utils.update(this::updateStatement, users, c -> c
				.set(deleted).equalTo(true)
				.where(id, isEqualTo(idValue))
		);
	}

	default List<UserData> query(UserQueryOptions options) {
		UserQueryOptions safeOptions = options == null ? UserQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, users, c -> c
				.where(id, isEqualToWhenPresent(() -> safeOptions.getId().orElse(null)))
				.and(firstName, isEqualToWhenPresent(() -> safeOptions.getFirstName().orElse(null)))
				.and(secondName, isEqualToWhenPresent(() -> safeOptions.getSecondName().orElse(null)))
				.and(phone, isEqualToWhenPresent(() -> safeOptions.getPhone().orElse(null)))
				.and(email, isEqualToWhenPresent(() -> safeOptions.getEmail().orElse(null)))
				.and(defaultAddressSlotId, isEqualToWhenPresent(() -> safeOptions.getDefaultAddressSlotId().orElse(null)))
				.and(enabled, isEqualToWhenPresent(() -> safeOptions.getEnabled().orElse(null)))
				.and(deleted, isEqualToWhenPresent(() -> safeOptions.getDeleted().orElse(null)))
				.and(createTime, isEqualToWhenPresent(() -> safeOptions.getCreateTime().orElse(null)))
				.and(updateTime, isEqualToWhenPresent(() -> safeOptions.getUpdateTime().orElse(null)))
				.orderBy(id)
		);
	}

}

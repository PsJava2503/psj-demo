package com.commerce.address.infrastructure.persistence.mapper;

import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.addresses;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.city;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.createTime;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.defaultAddress;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.deleted;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.detail;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.district;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.id;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.phone;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.province;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.recipientName;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.updateTime;
import static com.commerce.address.infrastructure.persistence.mapper.AddressDynamicSqlSupport.userId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.address.domain.model.AddressQueryOptions;
import com.commerce.address.infrastructure.persistence.model.AddressData;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface AddressDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, userId, recipientName, phone, province, city, district, detail, defaultAddress, deleted, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<AddressData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "AddressDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT),
			@Result(column = "recipient_name", property = "recipientName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "phone", property = "phone", jdbcType = JdbcType.VARCHAR),
			@Result(column = "province", property = "province", jdbcType = JdbcType.VARCHAR),
			@Result(column = "city", property = "city", jdbcType = JdbcType.VARCHAR),
			@Result(column = "district", property = "district", jdbcType = JdbcType.VARCHAR),
			@Result(column = "detail", property = "detail", jdbcType = JdbcType.VARCHAR),
			@Result(column = "is_default", property = "defaultAddress", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "deleted", property = "deleted", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<AddressData> selectMany(SelectStatementProvider selectStatement);

	default int create(AddressData data) {
		return MyBatis3Utils.insert(this::insert, data, addresses, c -> c
				.map(userId).toProperty("userId")
				.map(recipientName).toProperty("recipientName")
				.map(phone).toProperty("phone")
				.map(province).toProperty("province")
				.map(city).toProperty("city")
				.map(district).toProperty("district")
				.map(detail).toProperty("detail")
				.map(defaultAddress).toPropertyWhenPresent("defaultAddress", data::defaultAddress)
				.map(deleted).toPropertyWhenPresent("deleted", data::deleted)
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", data::updateTime)
		);
	}

	default int update(AddressData data, AddressQueryOptions options) {
		AddressQueryOptions safeOptions = options == null ? AddressQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("address update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, addresses, c -> c
				.set(userId).equalToWhenPresent(data::userId)
				.set(recipientName).equalToWhenPresent(data::recipientName)
				.set(phone).equalToWhenPresent(data::phone)
				.set(province).equalToWhenPresent(data::province)
				.set(city).equalToWhenPresent(data::city)
				.set(district).equalToWhenPresent(data::district)
				.set(detail).equalToWhenPresent(data::detail)
				.set(defaultAddress).equalToWhenPresent(data::defaultAddress)
				.set(deleted).equalToWhenPresent(data::deleted)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(phone, isEqualToWhenPresent(safeOptions::getPhoneValue))
				.and(defaultAddress, isEqualToWhenPresent(safeOptions::getDefaultAddressValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
		);
	}

	default int delete(AddressQueryOptions options) {
		AddressQueryOptions safeOptions = options == null ? AddressQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("address delete requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, addresses, c -> c
				.set(deleted).equalTo(true)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(phone, isEqualToWhenPresent(safeOptions::getPhoneValue))
				.and(defaultAddress, isEqualToWhenPresent(safeOptions::getDefaultAddressValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
		);
	}

	default List<AddressData> query(AddressQueryOptions options) {
		AddressQueryOptions safeOptions = options == null ? AddressQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, addresses, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(phone, isEqualToWhenPresent(safeOptions::getPhoneValue))
				.and(defaultAddress, isEqualToWhenPresent(safeOptions::getDefaultAddressValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
				.orderBy(id)
		);
	}
}

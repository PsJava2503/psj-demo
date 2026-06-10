package com.commerce.user.infrastructure.persistence.mapper;

import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.action;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.code;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.createTime;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.deleted;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.description;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.enabled;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.id;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.name;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.permissions;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.resource;
import static com.commerce.user.infrastructure.persistence.mapper.PermissionDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import com.commerce.user.domain.model.PermissionQueryOptions;
import com.commerce.user.infrastructure.persistence.model.PermissionData;
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
public interface PermissionDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, code, resource, action, name, description, enabled, deleted, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<PermissionData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "PermissionDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR),
			@Result(column = "resource", property = "resource", jdbcType = JdbcType.VARCHAR),
			@Result(column = "action", property = "action", jdbcType = JdbcType.VARCHAR),
			@Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
			@Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
			@Result(column = "enabled", property = "enabled", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "deleted", property = "deleted", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<PermissionData> selectMany(SelectStatementProvider selectStatement);

	default int create(PermissionData permissionData) {
		return MyBatis3Utils.insert(this::insert, permissionData, permissions, c -> c
				.map(code).toProperty("code")
				.map(resource).toProperty("resource")
				.map(action).toProperty("action")
				.map(name).toProperty("name")
				.map(description).toPropertyWhenPresent("description", permissionData::description)
				.map(enabled).toPropertyWhenPresent("enabled", permissionData::enabled)
				.map(deleted).toPropertyWhenPresent("deleted", permissionData::deleted)
				.map(createTime).toPropertyWhenPresent("createTime", permissionData::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", permissionData::updateTime)
		);
	}

	default int update(PermissionData permissionData) {
		return MyBatis3Utils.update(this::updateStatement, permissions, c -> c
				.set(code).equalToWhenPresent(permissionData::code)
				.set(resource).equalToWhenPresent(permissionData::resource)
				.set(action).equalToWhenPresent(permissionData::action)
				.set(name).equalToWhenPresent(permissionData::name)
				.set(description).equalToWhenPresent(permissionData::description)
				.set(enabled).equalToWhenPresent(permissionData::enabled)
				.set(deleted).equalToWhenPresent(permissionData::deleted)
				.set(updateTime).equalToWhenPresent(permissionData::updateTime)
				.where(id, isEqualTo(permissionData::id))
		);
	}

	default int delete(Long idValue) {
		return MyBatis3Utils.update(this::updateStatement, permissions, c -> c
				.set(deleted).equalTo(true)
				.where(id, isEqualTo(idValue))
		);
	}

	default List<PermissionData> query(PermissionQueryOptions options) {
		PermissionQueryOptions safeOptions = options == null ? PermissionQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, permissions, c -> c
				.where(id, isEqualToWhenPresent(() -> safeOptions.getId().orElse(null)))
				.and(code, isEqualToWhenPresent(() -> safeOptions.getCode().orElse(null)))
				.and(resource, isEqualToWhenPresent(() -> safeOptions.getResource().orElse(null)))
				.and(action, isEqualToWhenPresent(() -> safeOptions.getAction().orElse(null)))
				.and(enabled, isEqualToWhenPresent(() -> safeOptions.getEnabled().orElse(null)))
				.and(deleted, isEqualToWhenPresent(() -> safeOptions.getDeleted().orElse(null)))
				.orderBy(code)
		);
	}

	default List<PermissionData> selectActiveByIds(List<Long> permissionIds) {
		if (permissionIds == null || permissionIds.isEmpty()) {
			return List.of();
		}
		return MyBatis3Utils.selectList(this::selectMany, selectList, permissions, c -> c
				.where(id, isIn(permissionIds))
				.and(enabled, isEqualTo(true))
				.and(deleted, isEqualTo(false))
				.orderBy(code)
		);
	}

}

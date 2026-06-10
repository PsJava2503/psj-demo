package com.commerce.user.infrastructure.persistence.mapper;

import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.code;
import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.createTime;
import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.deleted;
import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.description;
import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.enabled;
import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.id;
import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.name;
import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.roles;
import static com.commerce.user.infrastructure.persistence.mapper.RoleDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import com.commerce.user.domain.model.RoleQueryOptions;
import com.commerce.user.infrastructure.persistence.model.RoleData;
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
public interface RoleDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(id, code, name, description, enabled, deleted, createTime, updateTime);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<RoleData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "RoleDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR),
			@Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
			@Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
			@Result(column = "enabled", property = "enabled", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "deleted", property = "deleted", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<RoleData> selectMany(SelectStatementProvider selectStatement);

	default int create(RoleData roleData) {
		return MyBatis3Utils.insert(this::insert, roleData, roles, c -> c
				.map(code).toProperty("code")
				.map(name).toProperty("name")
				.map(description).toPropertyWhenPresent("description", roleData::description)
				.map(enabled).toPropertyWhenPresent("enabled", roleData::enabled)
				.map(deleted).toPropertyWhenPresent("deleted", roleData::deleted)
				.map(createTime).toPropertyWhenPresent("createTime", roleData::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", roleData::updateTime)
		);
	}

	default int update(RoleData roleData) {
		return MyBatis3Utils.update(this::updateStatement, roles, c -> c
				.set(code).equalToWhenPresent(roleData::code)
				.set(name).equalToWhenPresent(roleData::name)
				.set(description).equalToWhenPresent(roleData::description)
				.set(enabled).equalToWhenPresent(roleData::enabled)
				.set(deleted).equalToWhenPresent(roleData::deleted)
				.set(updateTime).equalToWhenPresent(roleData::updateTime)
				.where(id, isEqualTo(roleData::id))
		);
	}

	default int delete(Long idValue) {
		return MyBatis3Utils.update(this::updateStatement, roles, c -> c
				.set(deleted).equalTo(true)
				.where(id, isEqualTo(idValue))
		);
	}

	default List<RoleData> query(RoleQueryOptions options) {
		RoleQueryOptions safeOptions = options == null ? RoleQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, roles, c -> c
				.where(id, isEqualToWhenPresent(() -> safeOptions.getId().orElse(null)))
				.and(code, isEqualToWhenPresent(() -> safeOptions.getCode().orElse(null)))
				.and(enabled, isEqualToWhenPresent(() -> safeOptions.getEnabled().orElse(null)))
				.and(deleted, isEqualToWhenPresent(() -> safeOptions.getDeleted().orElse(null)))
				.orderBy(code)
		);
	}

	default List<RoleData> selectActiveByIds(List<Long> roleIds) {
		if (roleIds == null || roleIds.isEmpty()) {
			return List.of();
		}
		return MyBatis3Utils.selectList(this::selectMany, selectList, roles, c -> c
				.where(id, isIn(roleIds))
				.and(enabled, isEqualTo(true))
				.and(deleted, isEqualTo(false))
				.orderBy(code)
		);
	}

}

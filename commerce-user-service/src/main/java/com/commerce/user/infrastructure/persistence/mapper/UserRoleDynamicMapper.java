package com.commerce.user.infrastructure.persistence.mapper;

import static com.commerce.user.infrastructure.persistence.mapper.UserRoleDynamicSqlSupport.assignedAt;
import static com.commerce.user.infrastructure.persistence.mapper.UserRoleDynamicSqlSupport.assignedBy;
import static com.commerce.user.infrastructure.persistence.mapper.UserRoleDynamicSqlSupport.roleId;
import static com.commerce.user.infrastructure.persistence.mapper.UserRoleDynamicSqlSupport.userId;
import static com.commerce.user.infrastructure.persistence.mapper.UserRoleDynamicSqlSupport.userRoles;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.user.domain.model.UserRoleQueryOptions;
import com.commerce.user.infrastructure.persistence.model.UserRoleData;
import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
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
public interface UserRoleDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(userId, roleId, assignedAt, assignedBy);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	int insert(InsertStatementProvider<UserRoleData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "UserRoleDataResult", value = {
			@Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT),
			@Result(column = "role_id", property = "roleId", jdbcType = JdbcType.BIGINT),
			@Result(column = "assigned_at", property = "assignedAt", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "assigned_by", property = "assignedBy", jdbcType = JdbcType.BIGINT)
	})
	List<UserRoleData> selectMany(SelectStatementProvider selectStatement);

	default int create(UserRoleData userRoleData) {
		return MyBatis3Utils.insert(this::insert, userRoleData, userRoles, c -> c
				.map(userId).toProperty("userId")
				.map(roleId).toProperty("roleId")
				.map(assignedAt).toPropertyWhenPresent("assignedAt", userRoleData::assignedAt)
				.map(assignedBy).toPropertyWhenPresent("assignedBy", userRoleData::assignedBy)
		);
	}

	default int update(UserRoleData userRoleData) {
		return MyBatis3Utils.update(this::updateStatement, userRoles, c -> c
				.set(assignedBy).equalToWhenPresent(userRoleData::assignedBy)
				.where(userId, isEqualTo(userRoleData::userId))
				.and(roleId, isEqualTo(userRoleData::roleId))
		);
	}

	default int delete(UserRoleData userRoleData) {
		return MyBatis3Utils.deleteFrom(this::deleteStatement, userRoles, c -> c
				.where(userId, isEqualTo(userRoleData::userId))
				.and(roleId, isEqualTo(userRoleData::roleId))
		);
	}

	default List<UserRoleData> query(UserRoleQueryOptions options) {
		UserRoleQueryOptions safeOptions = options == null ? UserRoleQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, userRoles, c -> c
				.where(userId, isEqualToWhenPresent(() -> safeOptions.getUserId().orElse(null)))
				.and(roleId, isEqualToWhenPresent(() -> safeOptions.getRoleId().orElse(null)))
				.orderBy(userId, roleId)
		);
	}

	default List<UserRoleData> selectByUserId(Long userIdValue) {
		return MyBatis3Utils.selectList(this::selectMany, selectList, userRoles, c -> c
				.where(userId, isEqualTo(userIdValue))
				.orderBy(roleId)
		);
	}

}

package com.commerce.user.infrastructure.persistence.mapper;

import static com.commerce.user.infrastructure.persistence.mapper.RolePermissionDynamicSqlSupport.assignedAt;
import static com.commerce.user.infrastructure.persistence.mapper.RolePermissionDynamicSqlSupport.assignedBy;
import static com.commerce.user.infrastructure.persistence.mapper.RolePermissionDynamicSqlSupport.permissionId;
import static com.commerce.user.infrastructure.persistence.mapper.RolePermissionDynamicSqlSupport.roleId;
import static com.commerce.user.infrastructure.persistence.mapper.RolePermissionDynamicSqlSupport.rolePermissions;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import com.commerce.user.domain.model.RolePermissionQueryOptions;
import com.commerce.user.infrastructure.persistence.model.RolePermissionData;
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
public interface RolePermissionDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(roleId, permissionId, assignedAt, assignedBy);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	int insert(InsertStatementProvider<RolePermissionData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "RolePermissionDataResult", value = {
			@Result(column = "role_id", property = "roleId", jdbcType = JdbcType.BIGINT),
			@Result(column = "permission_id", property = "permissionId", jdbcType = JdbcType.BIGINT),
			@Result(column = "assigned_at", property = "assignedAt", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "assigned_by", property = "assignedBy", jdbcType = JdbcType.BIGINT)
	})
	List<RolePermissionData> selectMany(SelectStatementProvider selectStatement);

	default int create(RolePermissionData rolePermissionData) {
		return MyBatis3Utils.insert(this::insert, rolePermissionData, rolePermissions, c -> c
				.map(roleId).toProperty("roleId")
				.map(permissionId).toProperty("permissionId")
				.map(assignedAt).toPropertyWhenPresent("assignedAt", rolePermissionData::assignedAt)
				.map(assignedBy).toPropertyWhenPresent("assignedBy", rolePermissionData::assignedBy)
		);
	}

	default int update(RolePermissionData rolePermissionData) {
		return MyBatis3Utils.update(this::updateStatement, rolePermissions, c -> c
				.set(assignedBy).equalToWhenPresent(rolePermissionData::assignedBy)
				.where(roleId, isEqualTo(rolePermissionData::roleId))
				.and(permissionId, isEqualTo(rolePermissionData::permissionId))
		);
	}

	default int delete(RolePermissionData rolePermissionData) {
		return MyBatis3Utils.deleteFrom(this::deleteStatement, rolePermissions, c -> c
				.where(roleId, isEqualTo(rolePermissionData::roleId))
				.and(permissionId, isEqualTo(rolePermissionData::permissionId))
		);
	}

	default List<RolePermissionData> query(RolePermissionQueryOptions options) {
		RolePermissionQueryOptions safeOptions = options == null ? RolePermissionQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, rolePermissions, c -> c
				.where(roleId, isEqualToWhenPresent(() -> safeOptions.getRoleId().orElse(null)))
				.and(permissionId, isEqualToWhenPresent(() -> safeOptions.getPermissionId().orElse(null)))
				.orderBy(roleId, permissionId)
		);
	}

	default List<RolePermissionData> selectByRoleIds(List<Long> roleIds) {
		if (roleIds == null || roleIds.isEmpty()) {
			return List.of();
		}
		return MyBatis3Utils.selectList(this::selectMany, selectList, rolePermissions, c -> c
				.where(roleId, isIn(roleIds))
				.orderBy(roleId, permissionId)
		);
	}

}

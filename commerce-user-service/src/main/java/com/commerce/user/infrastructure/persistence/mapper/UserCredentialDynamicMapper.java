package com.commerce.user.infrastructure.persistence.mapper;

import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.createTime;
import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.enabled;
import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.passwordAlgorithm;
import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.passwordHash;
import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.passwordSalt;
import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.updateTime;
import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.userCredentials;
import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.userId;
import static com.commerce.user.infrastructure.persistence.mapper.UserCredentialDynamicSqlSupport.username;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.user.domain.model.UserCredentialQueryOptions;
import com.commerce.user.infrastructure.persistence.model.UserCredentialData;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
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
public interface UserCredentialDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			userId, username, passwordHash, passwordSalt, passwordAlgorithm, enabled, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	int insert(InsertStatementProvider<UserCredentialData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "UserCredentialDataResult", value = {
			@Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "username", property = "username", jdbcType = JdbcType.VARCHAR),
			@Result(column = "password_hash", property = "passwordHash", jdbcType = JdbcType.VARCHAR),
			@Result(column = "password_salt", property = "passwordSalt", jdbcType = JdbcType.VARCHAR),
			@Result(column = "password_algorithm", property = "passwordAlgorithm", jdbcType = JdbcType.VARCHAR),
			@Result(column = "enabled", property = "enabled", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	Optional<UserCredentialData> selectOne(SelectStatementProvider selectStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@ResultMap("UserCredentialDataResult")
	List<UserCredentialData> selectMany(SelectStatementProvider selectStatement);

	default int create(UserCredentialData credentialData) {
		return MyBatis3Utils.insert(this::insert, credentialData, userCredentials, c -> c
				.map(userId).toProperty("userId")
				.map(username).toProperty("username")
				.map(passwordHash).toProperty("passwordHash")
				.map(passwordSalt).toProperty("passwordSalt")
				.map(passwordAlgorithm).toProperty("passwordAlgorithm")
				.map(enabled).toPropertyWhenPresent("enabled", credentialData::enabled)
				.map(createTime).toPropertyWhenPresent("createTime", credentialData::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", credentialData::updateTime)
		);
	}

	default int update(UserCredentialData credentialData) {
		return MyBatis3Utils.update(this::updateStatement, userCredentials, c -> c
				.set(username).equalToWhenPresent(credentialData::username)
				.set(passwordHash).equalToWhenPresent(credentialData::passwordHash)
				.set(passwordSalt).equalToWhenPresent(credentialData::passwordSalt)
				.set(passwordAlgorithm).equalToWhenPresent(credentialData::passwordAlgorithm)
				.set(enabled).equalToWhenPresent(credentialData::enabled)
				.set(updateTime).equalToWhenPresent(credentialData::updateTime)
				.where(userId, isEqualTo(credentialData::userId))
		);
	}

	default int delete(Long userIdValue) {
		return MyBatis3Utils.update(this::updateStatement, userCredentials, c -> c
				.set(enabled).equalTo(false)
				.where(userId, isEqualTo(userIdValue))
		);
	}

	default List<UserCredentialData> query(UserCredentialQueryOptions options) {
		UserCredentialQueryOptions safeOptions = options == null ? UserCredentialQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, userCredentials, c -> c
				.where(userId, isEqualToWhenPresent(() -> safeOptions.getUserId().orElse(null)))
				.and(username, isEqualToWhenPresent(() -> safeOptions.getUsername().orElse(null)))
				.and(enabled, isEqualToWhenPresent(() -> safeOptions.getEnabled().orElse(null)))
				.orderBy(userId)
		);
	}

	default Optional<UserCredentialData> selectByUsername(String usernameValue) {
		return MyBatis3Utils.selectOne(this::selectOne, selectList, userCredentials, c -> c
				.where(username, isEqualTo(usernameValue))
				.and(enabled, isEqualTo(true))
		);
	}

	default Optional<UserCredentialData> selectByUserId(Long userIdValue) {
		return MyBatis3Utils.selectOne(this::selectOne, selectList, userCredentials, c -> c
				.where(userId, isEqualTo(userIdValue))
				.and(enabled, isEqualTo(true))
		);
	}

}

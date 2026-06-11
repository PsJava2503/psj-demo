package com.commerce.payment.infrastructure.persistence.mapper;

import static com.commerce.payment.infrastructure.persistence.mapper.IdempotencyRecordDynamicSqlSupport.createTime;
import static com.commerce.payment.infrastructure.persistence.mapper.IdempotencyRecordDynamicSqlSupport.id;
import static com.commerce.payment.infrastructure.persistence.mapper.IdempotencyRecordDynamicSqlSupport.idempotencyKey;
import static com.commerce.payment.infrastructure.persistence.mapper.IdempotencyRecordDynamicSqlSupport.idempotencyRecord;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.payment.domain.model.IdempotencyRecordQueryOptions;
import com.commerce.payment.infrastructure.persistence.model.IdempotencyRecordData;
import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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
public interface IdempotencyRecordDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(id, idempotencyKey, createTime);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<IdempotencyRecordData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "IdempotencyRecordDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "idempotency_key", property = "idempotencyKey", jdbcType = JdbcType.VARCHAR),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<IdempotencyRecordData> selectMany(SelectStatementProvider selectStatement);

	default int create(IdempotencyRecordData data) {
		return MyBatis3Utils.insert(this::insert, data, idempotencyRecord, c -> c
				.map(idempotencyKey).toProperty("idempotencyKey")
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
		);
	}

	default int update(IdempotencyRecordData data, IdempotencyRecordQueryOptions options) {
		IdempotencyRecordQueryOptions safeOptions = options == null ? IdempotencyRecordQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("idempotency record update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, idempotencyRecord, c -> c
				.set(idempotencyKey).equalToWhenPresent(data::idempotencyKey)
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(idempotencyKey, isEqualToWhenPresent(safeOptions::getIdempotencyKeyValue))
		);
	}

	default int delete(IdempotencyRecordQueryOptions options) {
		IdempotencyRecordQueryOptions safeOptions = options == null ? IdempotencyRecordQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("idempotency record delete requires conditions");
		}
		return MyBatis3Utils.deleteFrom(this::deleteStatement, idempotencyRecord, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(idempotencyKey, isEqualToWhenPresent(safeOptions::getIdempotencyKeyValue))
		);
	}

	default List<IdempotencyRecordData> query(IdempotencyRecordQueryOptions options) {
		IdempotencyRecordQueryOptions safeOptions = options == null ? IdempotencyRecordQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, idempotencyRecord, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(idempotencyKey, isEqualToWhenPresent(safeOptions::getIdempotencyKeyValue))
				.orderBy(id)
		);
	}
}

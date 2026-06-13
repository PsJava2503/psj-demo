package com.commerce.order.infrastructure.persistence.mapper;

import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.aggregateId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.attemptCount;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.createTime;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.eventKey;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.eventType;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.id;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.lastError;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.nextRetryTime;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.orderOutboxEvents;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.payload;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.status;
import static com.commerce.order.infrastructure.persistence.mapper.OrderOutboxEventDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanWhenPresent;

import com.commerce.order.domain.model.OrderOutboxEventQueryOptions;
import com.commerce.order.infrastructure.persistence.model.OrderOutboxEventData;
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
public interface OrderOutboxEventDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, eventKey, eventType, aggregateId, payload, status, attemptCount, lastError, nextRetryTime, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<OrderOutboxEventData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "OrderOutboxEventDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "event_key", property = "eventKey", jdbcType = JdbcType.VARCHAR),
			@Result(column = "event_type", property = "eventType", jdbcType = JdbcType.VARCHAR),
			@Result(column = "aggregate_id", property = "aggregateId", jdbcType = JdbcType.BIGINT),
			@Result(column = "payload", property = "payload", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
			@Result(column = "attempt_count", property = "attemptCount", jdbcType = JdbcType.INTEGER),
			@Result(column = "last_error", property = "lastError", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "next_retry_time", property = "nextRetryTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<OrderOutboxEventData> selectMany(SelectStatementProvider selectStatement);

	default int create(OrderOutboxEventData data) {
		return MyBatis3Utils.insert(this::insert, data, orderOutboxEvents, c -> c
				.map(eventKey).toProperty("eventKey")
				.map(eventType).toProperty("eventType")
				.map(aggregateId).toProperty("aggregateId")
				.map(payload).toProperty("payload")
				.map(status).toProperty("status")
				.map(attemptCount).toPropertyWhenPresent("attemptCount", data::attemptCount)
				.map(lastError).toPropertyWhenPresent("lastError", data::lastError)
				.map(nextRetryTime).toPropertyWhenPresent("nextRetryTime", data::nextRetryTime)
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", data::updateTime)
		);
	}

	default int update(OrderOutboxEventData data, OrderOutboxEventQueryOptions options) {
		OrderOutboxEventQueryOptions safeOptions = options == null ? OrderOutboxEventQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("order outbox event update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, orderOutboxEvents, c -> c
				.set(status).equalToWhenPresent(data::status)
				.set(attemptCount).equalToWhenPresent(data::attemptCount)
				.set(lastError).equalToWhenPresent(data::lastError)
				.set(nextRetryTime).equalToWhenPresent(data::nextRetryTime)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(eventKey, isEqualToWhenPresent(safeOptions::getEventKeyValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(nextRetryTime, isLessThanWhenPresent(safeOptions::getNextRetryTimeBeforeValue))
		);
	}

	default int delete(OrderOutboxEventQueryOptions options) {
		return update(new OrderOutboxEventData(null, null, null, null, null, "DELETED", null, null, null, null, null), options);
	}

	default List<OrderOutboxEventData> query(OrderOutboxEventQueryOptions options) {
		OrderOutboxEventQueryOptions safeOptions = options == null ? OrderOutboxEventQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, orderOutboxEvents, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(eventKey, isEqualToWhenPresent(safeOptions::getEventKeyValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(nextRetryTime, isLessThanWhenPresent(safeOptions::getNextRetryTimeBeforeValue))
				.orderBy(id)
		);
	}
}

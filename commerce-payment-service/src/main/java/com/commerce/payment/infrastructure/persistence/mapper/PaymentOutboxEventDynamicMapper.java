package com.commerce.payment.infrastructure.persistence.mapper;

import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.aggregateId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.aggregateType;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.attemptCount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.createTime;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.eventKey;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.eventType;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.id;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.lastError;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.nextRetryTime;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.paymentOutboxEvent;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.payload;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.status;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOutboxEventDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanOrEqualToWhenPresent;

import com.commerce.payment.domain.model.PaymentOutboxEventQueryOptions;
import com.commerce.payment.infrastructure.persistence.model.PaymentOutboxEventData;
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
public interface PaymentOutboxEventDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, eventKey, eventType, aggregateType, aggregateId, payload, status, attemptCount, lastError,
			nextRetryTime, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<PaymentOutboxEventData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "PaymentOutboxEventDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "event_key", property = "eventKey", jdbcType = JdbcType.VARCHAR),
			@Result(column = "event_type", property = "eventType", jdbcType = JdbcType.VARCHAR),
			@Result(column = "aggregate_type", property = "aggregateType", jdbcType = JdbcType.VARCHAR),
			@Result(column = "aggregate_id", property = "aggregateId", jdbcType = JdbcType.VARCHAR),
			@Result(column = "payload", property = "payload", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
			@Result(column = "attempt_count", property = "attemptCount", jdbcType = JdbcType.INTEGER),
			@Result(column = "last_error", property = "lastError", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "next_retry_time", property = "nextRetryTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<PaymentOutboxEventData> selectMany(SelectStatementProvider selectStatement);

	default int create(PaymentOutboxEventData data) {
		return MyBatis3Utils.insert(this::insert, data, paymentOutboxEvent, c -> c
				.map(eventKey).toProperty("eventKey")
				.map(eventType).toProperty("eventType")
				.map(aggregateType).toProperty("aggregateType")
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

	default int update(PaymentOutboxEventData data, PaymentOutboxEventQueryOptions options) {
		PaymentOutboxEventQueryOptions safeOptions = options == null ? PaymentOutboxEventQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment outbox event update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, paymentOutboxEvent, c -> c
				.set(eventKey).equalToWhenPresent(data::eventKey)
				.set(eventType).equalToWhenPresent(data::eventType)
				.set(aggregateType).equalToWhenPresent(data::aggregateType)
				.set(aggregateId).equalToWhenPresent(data::aggregateId)
				.set(payload).equalToWhenPresent(data::payload)
				.set(status).equalToWhenPresent(data::status)
				.set(attemptCount).equalToWhenPresent(data::attemptCount)
				.set(lastError).equalToWhenPresent(data::lastError)
				.set(nextRetryTime).equalToWhenPresent(data::nextRetryTime)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(eventKey, isEqualToWhenPresent(safeOptions::getEventKeyValue))
				.and(eventType, isEqualToWhenPresent(safeOptions::getEventTypeValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(nextRetryTime, isLessThanOrEqualToWhenPresent(safeOptions::getNextRetryTimeBeforeValue))
		);
	}

	default int delete(PaymentOutboxEventQueryOptions options) {
		PaymentOutboxEventQueryOptions safeOptions = options == null ? PaymentOutboxEventQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment outbox event delete requires conditions");
		}
		return MyBatis3Utils.deleteFrom(this::deleteStatement, paymentOutboxEvent, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(eventKey, isEqualToWhenPresent(safeOptions::getEventKeyValue))
				.and(eventType, isEqualToWhenPresent(safeOptions::getEventTypeValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(nextRetryTime, isLessThanOrEqualToWhenPresent(safeOptions::getNextRetryTimeBeforeValue))
		);
	}

	default List<PaymentOutboxEventData> query(PaymentOutboxEventQueryOptions options) {
		PaymentOutboxEventQueryOptions safeOptions = options == null ? PaymentOutboxEventQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, paymentOutboxEvent, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(eventKey, isEqualToWhenPresent(safeOptions::getEventKeyValue))
				.and(eventType, isEqualToWhenPresent(safeOptions::getEventTypeValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(nextRetryTime, isLessThanOrEqualToWhenPresent(safeOptions::getNextRetryTimeBeforeValue))
				.orderBy(id)
		);
	}
}

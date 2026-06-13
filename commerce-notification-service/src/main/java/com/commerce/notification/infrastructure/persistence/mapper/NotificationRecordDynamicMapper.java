package com.commerce.notification.infrastructure.persistence.mapper;

import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.channel;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.createTime;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.errorMessage;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.id;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.idempotencyKey;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.notificationRecords;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.orderId;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.payload;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.recipient;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.sentTime;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.status;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.templateCode;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.updateTime;
import static com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicSqlSupport.userId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.notification.domain.model.NotificationRecordQueryOptions;
import com.commerce.notification.infrastructure.persistence.model.NotificationRecordData;
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
public interface NotificationRecordDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, orderId, userId, templateCode, channel, recipient, payload, status, idempotencyKey, sentTime,
			errorMessage, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<NotificationRecordData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "NotificationRecordDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "order_id", property = "orderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT),
			@Result(column = "template_code", property = "templateCode", jdbcType = JdbcType.VARCHAR),
			@Result(column = "channel", property = "channel", jdbcType = JdbcType.VARCHAR),
			@Result(column = "recipient", property = "recipient", jdbcType = JdbcType.VARCHAR),
			@Result(column = "payload", property = "payload", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
			@Result(column = "idempotency_key", property = "idempotencyKey", jdbcType = JdbcType.VARCHAR),
			@Result(column = "sent_time", property = "sentTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "error_message", property = "errorMessage", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<NotificationRecordData> selectMany(SelectStatementProvider selectStatement);

	default int create(NotificationRecordData data) {
		return MyBatis3Utils.insert(this::insert, data, notificationRecords, c -> c
				.map(orderId).toPropertyWhenPresent("orderId", data::orderId)
				.map(userId).toPropertyWhenPresent("userId", data::userId)
				.map(templateCode).toProperty("templateCode")
				.map(channel).toProperty("channel")
				.map(recipient).toPropertyWhenPresent("recipient", data::recipient)
				.map(payload).toPropertyWhenPresent("payload", data::payload)
				.map(status).toProperty("status")
				.map(idempotencyKey).toProperty("idempotencyKey")
				.map(sentTime).toPropertyWhenPresent("sentTime", data::sentTime)
				.map(errorMessage).toPropertyWhenPresent("errorMessage", data::errorMessage)
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", data::updateTime)
		);
	}

	default int update(NotificationRecordData data, NotificationRecordQueryOptions options) {
		NotificationRecordQueryOptions safeOptions = options == null ? NotificationRecordQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("notification record update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, notificationRecords, c -> c
				.set(status).equalToWhenPresent(data::status)
				.set(sentTime).equalToWhenPresent(data::sentTime)
				.set(errorMessage).equalToWhenPresent(data::errorMessage)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(idempotencyKey, isEqualToWhenPresent(safeOptions::getIdempotencyKeyValue))
		);
	}

	default int delete(NotificationRecordQueryOptions options) {
		return update(new NotificationRecordData(null, null, null, null, null, null, null, "DELETED", null, null, null, null, null), options);
	}

	default List<NotificationRecordData> query(NotificationRecordQueryOptions options) {
		NotificationRecordQueryOptions safeOptions = options == null ? NotificationRecordQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, notificationRecords, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(idempotencyKey, isEqualToWhenPresent(safeOptions::getIdempotencyKeyValue))
				.orderBy(id)
		);
	}
}

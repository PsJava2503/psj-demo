package com.commerce.payment.infrastructure.persistence.mapper;

import static com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicSqlSupport.createTime;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicSqlSupport.id;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicSqlSupport.notifyId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicSqlSupport.outTradeNo;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicSqlSupport.payload;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicSqlSupport.paymentNotifyLog;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicSqlSupport.tradeStatus;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentNotifyLogDynamicSqlSupport.verified;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.payment.domain.model.PaymentNotifyLogQueryOptions;
import com.commerce.payment.infrastructure.persistence.model.PaymentNotifyLogData;
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
public interface PaymentNotifyLogDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, notifyId, outTradeNo, tradeStatus, verified, payload, createTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<PaymentNotifyLogData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "PaymentNotifyLogDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "notify_id", property = "notifyId", jdbcType = JdbcType.VARCHAR),
			@Result(column = "out_trade_no", property = "outTradeNo", jdbcType = JdbcType.VARCHAR),
			@Result(column = "trade_status", property = "tradeStatus", jdbcType = JdbcType.VARCHAR),
			@Result(column = "verified", property = "verified", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "payload", property = "payload", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<PaymentNotifyLogData> selectMany(SelectStatementProvider selectStatement);

	default int create(PaymentNotifyLogData data) {
		return MyBatis3Utils.insert(this::insert, data, paymentNotifyLog, c -> c
				.map(notifyId).toPropertyWhenPresent("notifyId", data::notifyId)
				.map(outTradeNo).toPropertyWhenPresent("outTradeNo", data::outTradeNo)
				.map(tradeStatus).toPropertyWhenPresent("tradeStatus", data::tradeStatus)
				.map(verified).toProperty("verified")
				.map(payload).toProperty("payload")
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
		);
	}

	default int update(PaymentNotifyLogData data, PaymentNotifyLogQueryOptions options) {
		PaymentNotifyLogQueryOptions safeOptions = options == null ? PaymentNotifyLogQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment notify log update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, paymentNotifyLog, c -> c
				.set(notifyId).equalToWhenPresent(data::notifyId)
				.set(outTradeNo).equalToWhenPresent(data::outTradeNo)
				.set(tradeStatus).equalToWhenPresent(data::tradeStatus)
				.set(verified).equalToWhenPresent(data::verified)
				.set(payload).equalToWhenPresent(data::payload)
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(notifyId, isEqualToWhenPresent(safeOptions::getNotifyIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(tradeStatus, isEqualToWhenPresent(safeOptions::getTradeStatusValue))
				.and(verified, isEqualToWhenPresent(safeOptions::getVerifiedValue))
		);
	}

	default int delete(PaymentNotifyLogQueryOptions options) {
		PaymentNotifyLogQueryOptions safeOptions = options == null ? PaymentNotifyLogQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment notify log delete requires conditions");
		}
		return MyBatis3Utils.deleteFrom(this::deleteStatement, paymentNotifyLog, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(notifyId, isEqualToWhenPresent(safeOptions::getNotifyIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(tradeStatus, isEqualToWhenPresent(safeOptions::getTradeStatusValue))
				.and(verified, isEqualToWhenPresent(safeOptions::getVerifiedValue))
		);
	}

	default List<PaymentNotifyLogData> query(PaymentNotifyLogQueryOptions options) {
		PaymentNotifyLogQueryOptions safeOptions = options == null ? PaymentNotifyLogQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, paymentNotifyLog, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(notifyId, isEqualToWhenPresent(safeOptions::getNotifyIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(tradeStatus, isEqualToWhenPresent(safeOptions::getTradeStatusValue))
				.and(verified, isEqualToWhenPresent(safeOptions::getVerifiedValue))
				.orderBy(id)
		);
	}
}

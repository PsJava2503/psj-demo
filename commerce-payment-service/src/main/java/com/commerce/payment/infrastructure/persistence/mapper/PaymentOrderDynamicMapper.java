package com.commerce.payment.infrastructure.persistence.mapper;

import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.amount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.checkoutOrderId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.createTime;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.id;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.outTradeNo;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.paymentOrder;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.rawResponse;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.status;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.subject;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.tradeNo;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentOrderDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanWhenPresent;

import com.commerce.payment.domain.model.PaymentOrderQueryOptions;
import com.commerce.payment.infrastructure.persistence.model.PaymentOrderData;
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
public interface PaymentOrderDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, checkoutOrderId, outTradeNo, tradeNo, amount, subject, status, rawResponse, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<PaymentOrderData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "PaymentOrderDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "checkout_order_id", property = "checkoutOrderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "out_trade_no", property = "outTradeNo", jdbcType = JdbcType.VARCHAR),
			@Result(column = "trade_no", property = "tradeNo", jdbcType = JdbcType.VARCHAR),
			@Result(column = "amount", property = "amount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "subject", property = "subject", jdbcType = JdbcType.VARCHAR),
			@Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
			@Result(column = "raw_response", property = "rawResponse", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<PaymentOrderData> selectMany(SelectStatementProvider selectStatement);

	default int create(PaymentOrderData paymentOrderData) {
		return MyBatis3Utils.insert(this::insert, paymentOrderData, paymentOrder, c -> c
				.map(checkoutOrderId).toProperty("checkoutOrderId")
				.map(outTradeNo).toProperty("outTradeNo")
				.map(tradeNo).toPropertyWhenPresent("tradeNo", paymentOrderData::tradeNo)
				.map(amount).toProperty("amount")
				.map(subject).toProperty("subject")
				.map(status).toProperty("status")
				.map(rawResponse).toPropertyWhenPresent("rawResponse", paymentOrderData::rawResponse)
				.map(createTime).toPropertyWhenPresent("createTime", paymentOrderData::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", paymentOrderData::updateTime)
		);
	}

	default int update(PaymentOrderData paymentOrderData, PaymentOrderQueryOptions options) {
		PaymentOrderQueryOptions safeOptions = options == null ? PaymentOrderQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment order update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, paymentOrder, c -> c
				.set(tradeNo).equalToWhenPresent(paymentOrderData::tradeNo)
				.set(amount).equalToWhenPresent(paymentOrderData::amount)
				.set(subject).equalToWhenPresent(paymentOrderData::subject)
				.set(status).equalToWhenPresent(paymentOrderData::status)
				.set(rawResponse).equalToWhenPresent(paymentOrderData::rawResponse)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(createTime, isLessThanWhenPresent(safeOptions::getCreateTimeBeforeValue))
		);
	}

	default int delete(PaymentOrderQueryOptions options) {
		PaymentOrderQueryOptions safeOptions = options == null ? PaymentOrderQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment order delete requires conditions");
		}
		return MyBatis3Utils.deleteFrom(this::deleteStatement, paymentOrder, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(createTime, isLessThanWhenPresent(safeOptions::getCreateTimeBeforeValue))
		);
	}

	default List<PaymentOrderData> query(PaymentOrderQueryOptions options) {
		PaymentOrderQueryOptions safeOptions = options == null ? PaymentOrderQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, paymentOrder, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(createTime, isLessThanWhenPresent(safeOptions::getCreateTimeBeforeValue))
				.orderBy(id)
		);
	}
}

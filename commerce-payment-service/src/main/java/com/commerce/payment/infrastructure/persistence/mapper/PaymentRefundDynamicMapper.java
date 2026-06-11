package com.commerce.payment.infrastructure.persistence.mapper;

import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.createTime;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.checkoutOrderId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.id;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.outRefundNo;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.outTradeNo;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.paymentOrderId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.paymentRefund;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.rawResponse;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.refundAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundDynamicSqlSupport.status;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.payment.domain.model.PaymentRefundQueryOptions;
import com.commerce.payment.infrastructure.persistence.model.PaymentRefundData;
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
public interface PaymentRefundDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, paymentOrderId, checkoutOrderId, outTradeNo, outRefundNo, refundAmount, status, rawResponse, createTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<PaymentRefundData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "PaymentRefundDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "payment_order_id", property = "paymentOrderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "checkout_order_id", property = "checkoutOrderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "out_trade_no", property = "outTradeNo", jdbcType = JdbcType.VARCHAR),
			@Result(column = "out_refund_no", property = "outRefundNo", jdbcType = JdbcType.VARCHAR),
			@Result(column = "refund_amount", property = "refundAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
			@Result(column = "raw_response", property = "rawResponse", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<PaymentRefundData> selectMany(SelectStatementProvider selectStatement);

	default int create(PaymentRefundData data) {
		return MyBatis3Utils.insert(this::insert, data, paymentRefund, c -> c
				.map(paymentOrderId).toProperty("paymentOrderId")
				.map(checkoutOrderId).toProperty("checkoutOrderId")
				.map(outTradeNo).toProperty("outTradeNo")
				.map(outRefundNo).toProperty("outRefundNo")
				.map(refundAmount).toProperty("refundAmount")
				.map(status).toProperty("status")
				.map(rawResponse).toPropertyWhenPresent("rawResponse", data::rawResponse)
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
		);
	}

	default int update(PaymentRefundData data, PaymentRefundQueryOptions options) {
		PaymentRefundQueryOptions safeOptions = options == null ? PaymentRefundQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment refund update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, paymentRefund, c -> c
				.set(paymentOrderId).equalToWhenPresent(data::paymentOrderId)
				.set(checkoutOrderId).equalToWhenPresent(data::checkoutOrderId)
				.set(outTradeNo).equalToWhenPresent(data::outTradeNo)
				.set(outRefundNo).equalToWhenPresent(data::outRefundNo)
				.set(refundAmount).equalToWhenPresent(data::refundAmount)
				.set(status).equalToWhenPresent(data::status)
				.set(rawResponse).equalToWhenPresent(data::rawResponse)
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentOrderId, isEqualToWhenPresent(safeOptions::getPaymentOrderIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(outRefundNo, isEqualToWhenPresent(safeOptions::getOutRefundNoValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
		);
	}

	default int delete(PaymentRefundQueryOptions options) {
		PaymentRefundQueryOptions safeOptions = options == null ? PaymentRefundQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment refund delete requires conditions");
		}
		return MyBatis3Utils.deleteFrom(this::deleteStatement, paymentRefund, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentOrderId, isEqualToWhenPresent(safeOptions::getPaymentOrderIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(outRefundNo, isEqualToWhenPresent(safeOptions::getOutRefundNoValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
		);
	}

	default List<PaymentRefundData> query(PaymentRefundQueryOptions options) {
		PaymentRefundQueryOptions safeOptions = options == null ? PaymentRefundQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, paymentRefund, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentOrderId, isEqualToWhenPresent(safeOptions::getPaymentOrderIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(outTradeNo, isEqualToWhenPresent(safeOptions::getOutTradeNoValue))
				.and(outRefundNo, isEqualToWhenPresent(safeOptions::getOutRefundNoValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.orderBy(id)
		);
	}
}

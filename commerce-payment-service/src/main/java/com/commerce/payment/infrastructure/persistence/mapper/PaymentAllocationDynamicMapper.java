package com.commerce.payment.infrastructure.persistence.mapper;

import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.checkoutOrderId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.createTime;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.goodsAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.id;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.merchantDiscountAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.merchantId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.paidAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.paymentAllocation;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.paymentOrderId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.platformDiscountAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.settleAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.shippingAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.subOrderId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentAllocationDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.payment.domain.model.PaymentAllocationQueryOptions;
import com.commerce.payment.infrastructure.persistence.model.PaymentAllocationData;
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
public interface PaymentAllocationDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, paymentOrderId, checkoutOrderId, subOrderId, merchantId, goodsAmount, shippingAmount,
			platformDiscountAmount, merchantDiscountAmount, paidAmount, settleAmount, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<PaymentAllocationData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "PaymentAllocationDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "payment_order_id", property = "paymentOrderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "checkout_order_id", property = "checkoutOrderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "sub_order_id", property = "subOrderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "merchant_id", property = "merchantId", jdbcType = JdbcType.BIGINT),
			@Result(column = "goods_amount", property = "goodsAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "shipping_amount", property = "shippingAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "platform_discount_amount", property = "platformDiscountAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "merchant_discount_amount", property = "merchantDiscountAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "paid_amount", property = "paidAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "settle_amount", property = "settleAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<PaymentAllocationData> selectMany(SelectStatementProvider selectStatement);

	default int create(PaymentAllocationData data) {
		return MyBatis3Utils.insert(this::insert, data, paymentAllocation, c -> c
				.map(paymentOrderId).toProperty("paymentOrderId")
				.map(checkoutOrderId).toProperty("checkoutOrderId")
				.map(subOrderId).toProperty("subOrderId")
				.map(merchantId).toPropertyWhenPresent("merchantId", data::merchantId)
				.map(goodsAmount).toPropertyWhenPresent("goodsAmount", data::goodsAmount)
				.map(shippingAmount).toPropertyWhenPresent("shippingAmount", data::shippingAmount)
				.map(platformDiscountAmount).toPropertyWhenPresent("platformDiscountAmount", data::platformDiscountAmount)
				.map(merchantDiscountAmount).toPropertyWhenPresent("merchantDiscountAmount", data::merchantDiscountAmount)
				.map(paidAmount).toProperty("paidAmount")
				.map(settleAmount).toProperty("settleAmount")
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", data::updateTime)
		);
	}

	default int update(PaymentAllocationData data, PaymentAllocationQueryOptions options) {
		PaymentAllocationQueryOptions safeOptions = options == null ? PaymentAllocationQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment allocation update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, paymentAllocation, c -> c
				.set(merchantId).equalToWhenPresent(data::merchantId)
				.set(goodsAmount).equalToWhenPresent(data::goodsAmount)
				.set(shippingAmount).equalToWhenPresent(data::shippingAmount)
				.set(platformDiscountAmount).equalToWhenPresent(data::platformDiscountAmount)
				.set(merchantDiscountAmount).equalToWhenPresent(data::merchantDiscountAmount)
				.set(paidAmount).equalToWhenPresent(data::paidAmount)
				.set(settleAmount).equalToWhenPresent(data::settleAmount)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentOrderId, isEqualToWhenPresent(safeOptions::getPaymentOrderIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(subOrderId, isEqualToWhenPresent(safeOptions::getSubOrderIdValue))
				.and(merchantId, isEqualToWhenPresent(safeOptions::getMerchantIdValue))
		);
	}

	default int delete(PaymentAllocationQueryOptions options) {
		PaymentAllocationQueryOptions safeOptions = options == null ? PaymentAllocationQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment allocation delete requires conditions");
		}
		return MyBatis3Utils.deleteFrom(this::deleteStatement, paymentAllocation, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentOrderId, isEqualToWhenPresent(safeOptions::getPaymentOrderIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(subOrderId, isEqualToWhenPresent(safeOptions::getSubOrderIdValue))
				.and(merchantId, isEqualToWhenPresent(safeOptions::getMerchantIdValue))
		);
	}

	default List<PaymentAllocationData> query(PaymentAllocationQueryOptions options) {
		PaymentAllocationQueryOptions safeOptions = options == null ? PaymentAllocationQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, paymentAllocation, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentOrderId, isEqualToWhenPresent(safeOptions::getPaymentOrderIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(subOrderId, isEqualToWhenPresent(safeOptions::getSubOrderIdValue))
				.and(merchantId, isEqualToWhenPresent(safeOptions::getMerchantIdValue))
				.orderBy(id)
		);
	}
}

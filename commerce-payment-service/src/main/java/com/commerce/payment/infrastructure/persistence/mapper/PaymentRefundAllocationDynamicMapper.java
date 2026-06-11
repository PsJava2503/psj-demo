package com.commerce.payment.infrastructure.persistence.mapper;

import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.checkoutOrderId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.createTime;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.id;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.merchantId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.paymentAllocationId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.paymentRefundAllocation;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.paymentRefundId;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.refundGoodsAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.refundMerchantDiscountAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.refundPaidAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.refundPlatformDiscountAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.refundShippingAmount;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentRefundAllocationDynamicSqlSupport.subOrderId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.payment.domain.model.PaymentRefundAllocationQueryOptions;
import com.commerce.payment.infrastructure.persistence.model.PaymentRefundAllocationData;
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
public interface PaymentRefundAllocationDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, paymentRefundId, paymentAllocationId, checkoutOrderId, subOrderId, merchantId, refundGoodsAmount,
			refundShippingAmount, refundPlatformDiscountAmount, refundMerchantDiscountAmount, refundPaidAmount,
			createTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<PaymentRefundAllocationData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "PaymentRefundAllocationDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "payment_refund_id", property = "paymentRefundId", jdbcType = JdbcType.BIGINT),
			@Result(column = "payment_allocation_id", property = "paymentAllocationId", jdbcType = JdbcType.BIGINT),
			@Result(column = "checkout_order_id", property = "checkoutOrderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "sub_order_id", property = "subOrderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "merchant_id", property = "merchantId", jdbcType = JdbcType.BIGINT),
			@Result(column = "refund_goods_amount", property = "refundGoodsAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "refund_shipping_amount", property = "refundShippingAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "refund_platform_discount_amount", property = "refundPlatformDiscountAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "refund_merchant_discount_amount", property = "refundMerchantDiscountAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "refund_paid_amount", property = "refundPaidAmount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<PaymentRefundAllocationData> selectMany(SelectStatementProvider selectStatement);

	default int create(PaymentRefundAllocationData data) {
		return MyBatis3Utils.insert(this::insert, data, paymentRefundAllocation, c -> c
				.map(paymentRefundId).toProperty("paymentRefundId")
				.map(paymentAllocationId).toPropertyWhenPresent("paymentAllocationId", data::paymentAllocationId)
				.map(checkoutOrderId).toProperty("checkoutOrderId")
				.map(subOrderId).toProperty("subOrderId")
				.map(merchantId).toPropertyWhenPresent("merchantId", data::merchantId)
				.map(refundGoodsAmount).toPropertyWhenPresent("refundGoodsAmount", data::refundGoodsAmount)
				.map(refundShippingAmount).toPropertyWhenPresent("refundShippingAmount", data::refundShippingAmount)
				.map(refundPlatformDiscountAmount).toPropertyWhenPresent("refundPlatformDiscountAmount", data::refundPlatformDiscountAmount)
				.map(refundMerchantDiscountAmount).toPropertyWhenPresent("refundMerchantDiscountAmount", data::refundMerchantDiscountAmount)
				.map(refundPaidAmount).toProperty("refundPaidAmount")
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
		);
	}

	default int update(PaymentRefundAllocationData data, PaymentRefundAllocationQueryOptions options) {
		PaymentRefundAllocationQueryOptions safeOptions = options == null ? PaymentRefundAllocationQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment refund allocation update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, paymentRefundAllocation, c -> c
				.set(paymentAllocationId).equalToWhenPresent(data::paymentAllocationId)
				.set(merchantId).equalToWhenPresent(data::merchantId)
				.set(refundGoodsAmount).equalToWhenPresent(data::refundGoodsAmount)
				.set(refundShippingAmount).equalToWhenPresent(data::refundShippingAmount)
				.set(refundPlatformDiscountAmount).equalToWhenPresent(data::refundPlatformDiscountAmount)
				.set(refundMerchantDiscountAmount).equalToWhenPresent(data::refundMerchantDiscountAmount)
				.set(refundPaidAmount).equalToWhenPresent(data::refundPaidAmount)
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentRefundId, isEqualToWhenPresent(safeOptions::getPaymentRefundIdValue))
				.and(paymentAllocationId, isEqualToWhenPresent(safeOptions::getPaymentAllocationIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(subOrderId, isEqualToWhenPresent(safeOptions::getSubOrderIdValue))
				.and(merchantId, isEqualToWhenPresent(safeOptions::getMerchantIdValue))
		);
	}

	default int delete(PaymentRefundAllocationQueryOptions options) {
		PaymentRefundAllocationQueryOptions safeOptions = options == null ? PaymentRefundAllocationQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment refund allocation delete requires conditions");
		}
		return MyBatis3Utils.deleteFrom(this::deleteStatement, paymentRefundAllocation, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentRefundId, isEqualToWhenPresent(safeOptions::getPaymentRefundIdValue))
				.and(paymentAllocationId, isEqualToWhenPresent(safeOptions::getPaymentAllocationIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(subOrderId, isEqualToWhenPresent(safeOptions::getSubOrderIdValue))
				.and(merchantId, isEqualToWhenPresent(safeOptions::getMerchantIdValue))
		);
	}

	default List<PaymentRefundAllocationData> query(PaymentRefundAllocationQueryOptions options) {
		PaymentRefundAllocationQueryOptions safeOptions = options == null ? PaymentRefundAllocationQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, paymentRefundAllocation, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(paymentRefundId, isEqualToWhenPresent(safeOptions::getPaymentRefundIdValue))
				.and(paymentAllocationId, isEqualToWhenPresent(safeOptions::getPaymentAllocationIdValue))
				.and(checkoutOrderId, isEqualToWhenPresent(safeOptions::getCheckoutOrderIdValue))
				.and(subOrderId, isEqualToWhenPresent(safeOptions::getSubOrderIdValue))
				.and(merchantId, isEqualToWhenPresent(safeOptions::getMerchantIdValue))
				.orderBy(id)
		);
	}
}

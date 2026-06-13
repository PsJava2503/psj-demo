package com.commerce.order.infrastructure.persistence.mapper;

import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.addressDetail;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.addressId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.amount;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.city;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.createTime;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.district;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.id;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.orderNo;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.orders;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.productId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.productName;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.province;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.quantity;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.recipientName;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.recipientPhone;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.skuId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.status;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.unitPrice;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.updateTime;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.userId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderDynamicSqlSupport.version;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanWhenPresent;

import com.commerce.order.domain.model.OrderQueryOptions;
import com.commerce.order.infrastructure.persistence.model.OrderData;
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
public interface OrderDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, orderNo, userId, productId, skuId, productName, unitPrice, quantity, amount, addressId, recipientName,
			recipientPhone, province, city, district, addressDetail, status, version, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = false)
	int insert(InsertStatementProvider<OrderData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "OrderDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "order_no", property = "orderNo", jdbcType = JdbcType.VARCHAR),
			@Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT),
			@Result(column = "product_id", property = "productId", jdbcType = JdbcType.BIGINT),
			@Result(column = "sku_id", property = "skuId", jdbcType = JdbcType.BIGINT),
			@Result(column = "product_name", property = "productName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "unit_price", property = "unitPrice", jdbcType = JdbcType.DECIMAL),
			@Result(column = "quantity", property = "quantity", jdbcType = JdbcType.INTEGER),
			@Result(column = "amount", property = "amount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "address_id", property = "addressId", jdbcType = JdbcType.BIGINT),
			@Result(column = "recipient_name", property = "recipientName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "recipient_phone", property = "recipientPhone", jdbcType = JdbcType.VARCHAR),
			@Result(column = "province", property = "province", jdbcType = JdbcType.VARCHAR),
			@Result(column = "city", property = "city", jdbcType = JdbcType.VARCHAR),
			@Result(column = "district", property = "district", jdbcType = JdbcType.VARCHAR),
			@Result(column = "address_detail", property = "addressDetail", jdbcType = JdbcType.VARCHAR),
			@Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
			@Result(column = "version", property = "version", jdbcType = JdbcType.BIGINT),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<OrderData> selectMany(SelectStatementProvider selectStatement);

	default int create(OrderData data) {
		return MyBatis3Utils.insert(this::insert, data, orders, c -> c
				.map(id).toProperty("id")
				.map(orderNo).toProperty("orderNo")
				.map(userId).toProperty("userId")
				.map(productId).toProperty("productId")
				.map(skuId).toPropertyWhenPresent("skuId", data::skuId)
				.map(productName).toProperty("productName")
				.map(unitPrice).toProperty("unitPrice")
				.map(quantity).toProperty("quantity")
				.map(amount).toProperty("amount")
				.map(addressId).toPropertyWhenPresent("addressId", data::addressId)
				.map(recipientName).toPropertyWhenPresent("recipientName", data::recipientName)
				.map(recipientPhone).toPropertyWhenPresent("recipientPhone", data::recipientPhone)
				.map(province).toPropertyWhenPresent("province", data::province)
				.map(city).toPropertyWhenPresent("city", data::city)
				.map(district).toPropertyWhenPresent("district", data::district)
				.map(addressDetail).toPropertyWhenPresent("addressDetail", data::addressDetail)
				.map(status).toProperty("status")
				.map(version).toPropertyWhenPresent("version", data::version)
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", data::updateTime)
		);
	}

	default int update(OrderData data, OrderQueryOptions options) {
		OrderQueryOptions safeOptions = options == null ? OrderQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("order update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, orders, c -> c
				.set(status).equalToWhenPresent(data::status)
				.set(recipientName).equalToWhenPresent(data::recipientName)
				.set(recipientPhone).equalToWhenPresent(data::recipientPhone)
				.set(province).equalToWhenPresent(data::province)
				.set(city).equalToWhenPresent(data::city)
				.set(district).equalToWhenPresent(data::district)
				.set(addressDetail).equalToWhenPresent(data::addressDetail)
				.set(version).equalToConstant("version + 1")
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(version, isEqualToWhenPresent(data::version))
				.and(createTime, isLessThanWhenPresent(safeOptions::getCreateTimeBeforeValue))
		);
	}

	default int delete(OrderQueryOptions options) {
		OrderQueryOptions safeOptions = options == null ? OrderQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("order delete requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, orders, c -> c
				.set(status).equalTo("CANCELLED")
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(createTime, isLessThanWhenPresent(safeOptions::getCreateTimeBeforeValue))
		);
	}

	default List<OrderData> query(OrderQueryOptions options) {
		OrderQueryOptions safeOptions = options == null ? OrderQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, orders, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.and(createTime, isLessThanWhenPresent(safeOptions::getCreateTimeBeforeValue))
				.orderBy(id.descending())
		);
	}
}

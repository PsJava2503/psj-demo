package com.commerce.order.infrastructure.persistence.mapper;

import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.amount;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.createTime;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.id;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.orderId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.orderItems;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.productId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.productName;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.quantity;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.skuId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderItemDynamicSqlSupport.unitPrice;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.order.domain.model.OrderItemQueryOptions;
import com.commerce.order.infrastructure.persistence.model.OrderItemData;
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
public interface OrderItemDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(id, orderId, productId, skuId, productName, unitPrice, quantity, amount, createTime);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<OrderItemData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "OrderItemDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "order_id", property = "orderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "product_id", property = "productId", jdbcType = JdbcType.BIGINT),
			@Result(column = "sku_id", property = "skuId", jdbcType = JdbcType.BIGINT),
			@Result(column = "product_name", property = "productName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "unit_price", property = "unitPrice", jdbcType = JdbcType.DECIMAL),
			@Result(column = "quantity", property = "quantity", jdbcType = JdbcType.INTEGER),
			@Result(column = "amount", property = "amount", jdbcType = JdbcType.DECIMAL),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<OrderItemData> selectMany(SelectStatementProvider selectStatement);

	default int create(OrderItemData data) {
		return MyBatis3Utils.insert(this::insert, data, orderItems, c -> c
				.map(orderId).toProperty("orderId")
				.map(productId).toProperty("productId")
				.map(skuId).toPropertyWhenPresent("skuId", data::skuId)
				.map(productName).toProperty("productName")
				.map(unitPrice).toProperty("unitPrice")
				.map(quantity).toProperty("quantity")
				.map(amount).toProperty("amount")
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
		);
	}

	default int update(OrderItemData data, OrderItemQueryOptions options) {
		OrderItemQueryOptions safeOptions = options == null ? OrderItemQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("order item update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, orderItems, c -> c
				.set(productName).equalToWhenPresent(data::productName)
				.set(unitPrice).equalToWhenPresent(data::unitPrice)
				.set(quantity).equalToWhenPresent(data::quantity)
				.set(amount).equalToWhenPresent(data::amount)
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(productId, isEqualToWhenPresent(safeOptions::getProductIdValue))
				.and(skuId, isEqualToWhenPresent(safeOptions::getSkuIdValue))
		);
	}

	default int delete(OrderItemQueryOptions options) {
		OrderItemQueryOptions safeOptions = options == null ? OrderItemQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("order item delete requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, orderItems, c -> c
				.set(quantity).equalTo(0)
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(productId, isEqualToWhenPresent(safeOptions::getProductIdValue))
				.and(skuId, isEqualToWhenPresent(safeOptions::getSkuIdValue))
		);
	}

	default List<OrderItemData> query(OrderItemQueryOptions options) {
		OrderItemQueryOptions safeOptions = options == null ? OrderItemQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, orderItems, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(productId, isEqualToWhenPresent(safeOptions::getProductIdValue))
				.and(skuId, isEqualToWhenPresent(safeOptions::getSkuIdValue))
				.orderBy(id)
		);
	}
}

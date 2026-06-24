package com.commerce.cart.infrastructure.persistence.mapper;

import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.cartItems;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.createTime;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.deleted;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.id;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.productId;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.productName;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.quantity;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.selected;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.skuId;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.unitPrice;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.updateTime;
import static com.commerce.cart.infrastructure.persistence.mapper.CartItemDynamicSqlSupport.userId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.cart.domain.model.CartItemQueryOptions;
import com.commerce.cart.infrastructure.persistence.model.CartItemData;
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
public interface CartItemDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, userId, productId, skuId, productName, unitPrice, quantity, selected, deleted, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<CartItemData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "CartItemDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT),
			@Result(column = "product_id", property = "productId", jdbcType = JdbcType.BIGINT),
			@Result(column = "sku_id", property = "skuId", jdbcType = JdbcType.BIGINT),
			@Result(column = "product_name", property = "productName", jdbcType = JdbcType.VARCHAR),
			@Result(column = "unit_price", property = "unitPrice", jdbcType = JdbcType.DECIMAL),
			@Result(column = "quantity", property = "quantity", jdbcType = JdbcType.INTEGER),
			@Result(column = "selected", property = "selected", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "deleted", property = "deleted", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<CartItemData> selectMany(SelectStatementProvider selectStatement);

	default int create(CartItemData data) {
		return MyBatis3Utils.insert(this::insert, data, cartItems, c -> c
				.map(userId).toProperty("userId")
				.map(productId).toProperty("productId")
				.map(skuId).toProperty("skuId")
				.map(productName).toProperty("productName")
				.map(unitPrice).toProperty("unitPrice")
				.map(quantity).toProperty("quantity")
				.map(selected).toPropertyWhenPresent("selected", data::selected)
				.map(deleted).toPropertyWhenPresent("deleted", data::deleted)
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", data::updateTime)
		);
	}

	default int update(CartItemData data, CartItemQueryOptions options) {
		CartItemQueryOptions safeOptions = options == null ? CartItemQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("cart item update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, cartItems, c -> c
				.set(userId).equalToWhenPresent(data::userId)
				.set(productId).equalToWhenPresent(data::productId)
				.set(skuId).equalToWhenPresent(data::skuId)
				.set(productName).equalToWhenPresent(data::productName)
				.set(unitPrice).equalToWhenPresent(data::unitPrice)
				.set(quantity).equalToWhenPresent(data::quantity)
				.set(selected).equalToWhenPresent(data::selected)
				.set(deleted).equalToWhenPresent(data::deleted)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(productId, isEqualToWhenPresent(safeOptions::getProductIdValue))
				.and(selected, isEqualToWhenPresent(safeOptions::getSelectedValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
		);
	}

	default int delete(CartItemQueryOptions options) {
		CartItemQueryOptions safeOptions = options == null ? CartItemQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("cart item delete requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, cartItems, c -> c
				.set(deleted).equalTo(true)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(productId, isEqualToWhenPresent(safeOptions::getProductIdValue))
				.and(selected, isEqualToWhenPresent(safeOptions::getSelectedValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
		);
	}

	default List<CartItemData> query(CartItemQueryOptions options) {
		CartItemQueryOptions safeOptions = options == null ? CartItemQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, cartItems, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(userId, isEqualToWhenPresent(safeOptions::getUserIdValue))
				.and(productId, isEqualToWhenPresent(safeOptions::getProductIdValue))
				.and(selected, isEqualToWhenPresent(safeOptions::getSelectedValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
				.orderBy(id)
		);
	}
}

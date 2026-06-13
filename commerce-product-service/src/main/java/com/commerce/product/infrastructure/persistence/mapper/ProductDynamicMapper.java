package com.commerce.product.infrastructure.persistence.mapper;

import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.createTime;
import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.deleted;
import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.enabled;
import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.id;
import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.name;
import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.price;
import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.products;
import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.skuId;
import static com.commerce.product.infrastructure.persistence.mapper.ProductDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.product.domain.model.ProductQueryOptions;
import com.commerce.product.infrastructure.persistence.model.ProductData;
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
public interface ProductDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(id, name, price, skuId, enabled, deleted, createTime, updateTime);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<ProductData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "ProductDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
			@Result(column = "price", property = "price", jdbcType = JdbcType.DECIMAL),
			@Result(column = "sku_id", property = "skuId", jdbcType = JdbcType.BIGINT),
			@Result(column = "enabled", property = "enabled", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "deleted", property = "deleted", jdbcType = JdbcType.BOOLEAN),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<ProductData> selectMany(SelectStatementProvider selectStatement);

	default int create(ProductData productData) {
		return MyBatis3Utils.insert(this::insert, productData, products, c -> c
				.map(name).toProperty("name")
				.map(price).toProperty("price")
				.map(skuId).toPropertyWhenPresent("skuId", productData::skuId)
				.map(enabled).toPropertyWhenPresent("enabled", productData::enabled)
				.map(deleted).toPropertyWhenPresent("deleted", productData::deleted)
				.map(createTime).toPropertyWhenPresent("createTime", productData::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", productData::updateTime)
		);
	}

	default int update(ProductData productData, ProductQueryOptions options) {
		ProductQueryOptions safeOptions = options == null ? ProductQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("product update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, products, c -> c
				.set(name).equalToWhenPresent(productData::name)
				.set(price).equalToWhenPresent(productData::price)
				.set(skuId).equalToWhenPresent(productData::skuId)
				.set(enabled).equalToWhenPresent(productData::enabled)
				.set(deleted).equalToWhenPresent(productData::deleted)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(name, isEqualToWhenPresent(safeOptions::getNameValue))
				.and(skuId, isEqualToWhenPresent(safeOptions::getSkuIdValue))
				.and(enabled, isEqualToWhenPresent(safeOptions::getEnabledValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
		);
	}

	default int delete(ProductQueryOptions options) {
		ProductQueryOptions safeOptions = options == null ? ProductQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("product delete requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, products, c -> c
				.set(deleted).equalTo(true)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(name, isEqualToWhenPresent(safeOptions::getNameValue))
				.and(skuId, isEqualToWhenPresent(safeOptions::getSkuIdValue))
				.and(enabled, isEqualToWhenPresent(safeOptions::getEnabledValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
		);
	}

	default List<ProductData> query(ProductQueryOptions options) {
		ProductQueryOptions safeOptions = options == null ? ProductQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, products, c -> c
				.where(deleted, isEqualTo(false))
				.and(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(name, isEqualToWhenPresent(safeOptions::getNameValue))
				.and(skuId, isEqualToWhenPresent(safeOptions::getSkuIdValue))
				.and(enabled, isEqualToWhenPresent(safeOptions::getEnabledValue))
				.and(deleted, isEqualToWhenPresent(safeOptions::getDeletedValue))
				.orderBy(id)
		);
	}
}

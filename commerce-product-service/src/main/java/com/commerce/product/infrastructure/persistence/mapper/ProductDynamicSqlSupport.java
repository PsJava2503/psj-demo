package com.commerce.product.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ProductDynamicSqlSupport {

	public static final ProductTable products = new ProductTable();
	public static final SqlColumn<Long> id = products.id;
	public static final SqlColumn<String> name = products.name;
	public static final SqlColumn<BigDecimal> price = products.price;
	public static final SqlColumn<Long> skuId = products.skuId;
	public static final SqlColumn<Boolean> enabled = products.enabled;
	public static final SqlColumn<Boolean> deleted = products.deleted;
	public static final SqlColumn<ZonedDateTime> createTime = products.createTime;
	public static final SqlColumn<ZonedDateTime> updateTime = products.updateTime;

	private ProductDynamicSqlSupport() {
	}

	public static final class ProductTable extends SqlTable {

		public final SqlColumn<Long> id = column("id");
		public final SqlColumn<String> name = column("name");
		public final SqlColumn<BigDecimal> price = column("price");
		public final SqlColumn<Long> skuId = column("sku_id");
		public final SqlColumn<Boolean> enabled = column("enabled");
		public final SqlColumn<Boolean> deleted = column("deleted");
		public final SqlColumn<ZonedDateTime> createTime = column("create_time");
		public final SqlColumn<ZonedDateTime> updateTime = column("update_time");

		public ProductTable() {
			super("products");
		}
	}
}

package com.commerce.order.infrastructure.persistence.mapper;

import static com.commerce.order.infrastructure.persistence.mapper.OrderStatusLogDynamicSqlSupport.createTime;
import static com.commerce.order.infrastructure.persistence.mapper.OrderStatusLogDynamicSqlSupport.fromStatus;
import static com.commerce.order.infrastructure.persistence.mapper.OrderStatusLogDynamicSqlSupport.id;
import static com.commerce.order.infrastructure.persistence.mapper.OrderStatusLogDynamicSqlSupport.orderId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderStatusLogDynamicSqlSupport.orderStatusLogs;
import static com.commerce.order.infrastructure.persistence.mapper.OrderStatusLogDynamicSqlSupport.reason;
import static com.commerce.order.infrastructure.persistence.mapper.OrderStatusLogDynamicSqlSupport.toStatus;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.order.domain.model.OrderStatusLogQueryOptions;
import com.commerce.order.infrastructure.persistence.model.OrderStatusLogData;
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
public interface OrderStatusLogDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(id, orderId, fromStatus, toStatus, reason, createTime);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<OrderStatusLogData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "OrderStatusLogDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "order_id", property = "orderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "from_status", property = "fromStatus", jdbcType = JdbcType.VARCHAR),
			@Result(column = "to_status", property = "toStatus", jdbcType = JdbcType.VARCHAR),
			@Result(column = "reason", property = "reason", jdbcType = JdbcType.VARCHAR),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<OrderStatusLogData> selectMany(SelectStatementProvider selectStatement);

	default int create(OrderStatusLogData data) {
		return MyBatis3Utils.insert(this::insert, data, orderStatusLogs, c -> c
				.map(orderId).toProperty("orderId")
				.map(fromStatus).toPropertyWhenPresent("fromStatus", data::fromStatus)
				.map(toStatus).toProperty("toStatus")
				.map(reason).toPropertyWhenPresent("reason", data::reason)
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
		);
	}

	default int update(OrderStatusLogData data, OrderStatusLogQueryOptions options) {
		OrderStatusLogQueryOptions safeOptions = options == null ? OrderStatusLogQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("order status log update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, orderStatusLogs, c -> c
				.set(reason).equalToWhenPresent(data::reason)
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(toStatus, isEqualToWhenPresent(safeOptions::getToStatusValue))
		);
	}

	default int delete(OrderStatusLogQueryOptions options) {
		return update(new OrderStatusLogData(null, null, null, null, "DELETED", null), options);
	}

	default List<OrderStatusLogData> query(OrderStatusLogQueryOptions options) {
		OrderStatusLogQueryOptions safeOptions = options == null ? OrderStatusLogQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, orderStatusLogs, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(toStatus, isEqualToWhenPresent(safeOptions::getToStatusValue))
				.orderBy(id)
		);
	}
}

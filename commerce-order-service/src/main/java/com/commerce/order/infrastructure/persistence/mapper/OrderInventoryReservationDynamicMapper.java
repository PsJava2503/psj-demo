package com.commerce.order.infrastructure.persistence.mapper;

import static com.commerce.order.infrastructure.persistence.mapper.OrderInventoryReservationDynamicSqlSupport.createTime;
import static com.commerce.order.infrastructure.persistence.mapper.OrderInventoryReservationDynamicSqlSupport.id;
import static com.commerce.order.infrastructure.persistence.mapper.OrderInventoryReservationDynamicSqlSupport.orderId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderInventoryReservationDynamicSqlSupport.orderInventoryReservations;
import static com.commerce.order.infrastructure.persistence.mapper.OrderInventoryReservationDynamicSqlSupport.reservationId;
import static com.commerce.order.infrastructure.persistence.mapper.OrderInventoryReservationDynamicSqlSupport.status;
import static com.commerce.order.infrastructure.persistence.mapper.OrderInventoryReservationDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.order.domain.model.OrderInventoryReservationQueryOptions;
import com.commerce.order.infrastructure.persistence.model.OrderInventoryReservationData;
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
public interface OrderInventoryReservationDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(id, orderId, reservationId, status, createTime, updateTime);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<OrderInventoryReservationData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "OrderInventoryReservationDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "order_id", property = "orderId", jdbcType = JdbcType.BIGINT),
			@Result(column = "reservation_id", property = "reservationId", jdbcType = JdbcType.BIGINT),
			@Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<OrderInventoryReservationData> selectMany(SelectStatementProvider selectStatement);

	default int create(OrderInventoryReservationData data) {
		return MyBatis3Utils.insert(this::insert, data, orderInventoryReservations, c -> c
				.map(orderId).toProperty("orderId")
				.map(reservationId).toProperty("reservationId")
				.map(status).toProperty("status")
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", data::updateTime)
		);
	}

	default int update(OrderInventoryReservationData data, OrderInventoryReservationQueryOptions options) {
		OrderInventoryReservationQueryOptions safeOptions = options == null ? OrderInventoryReservationQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("order inventory reservation update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, orderInventoryReservations, c -> c
				.set(status).equalToWhenPresent(data::status)
				.set(updateTime).equalToConstant("NOW()")
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(reservationId, isEqualToWhenPresent(safeOptions::getReservationIdValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
		);
	}

	default int delete(OrderInventoryReservationQueryOptions options) {
		return update(new OrderInventoryReservationData(null, null, null, "CANCELLED", null, null), options);
	}

	default List<OrderInventoryReservationData> query(OrderInventoryReservationQueryOptions options) {
		OrderInventoryReservationQueryOptions safeOptions = options == null ? OrderInventoryReservationQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, orderInventoryReservations, c -> c
				.where(id, isEqualToWhenPresent(safeOptions::getIdValue))
				.and(orderId, isEqualToWhenPresent(safeOptions::getOrderIdValue))
				.and(reservationId, isEqualToWhenPresent(safeOptions::getReservationIdValue))
				.and(status, isEqualToWhenPresent(safeOptions::getStatusValue))
				.orderBy(id)
		);
	}
}

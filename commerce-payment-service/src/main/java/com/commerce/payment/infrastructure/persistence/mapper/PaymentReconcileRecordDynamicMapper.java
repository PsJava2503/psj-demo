package com.commerce.payment.infrastructure.persistence.mapper;

import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.billDate;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.billType;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.createTime;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.downloadUrl;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.id;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.paymentReconcileRecord;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.rawResponse;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.status;
import static com.commerce.payment.infrastructure.persistence.mapper.PaymentReconcileRecordDynamicSqlSupport.updateTime;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

import com.commerce.payment.domain.model.PaymentReconcileRecordQueryOptions;
import com.commerce.payment.infrastructure.persistence.model.PaymentReconcileRecordData;
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
public interface PaymentReconcileRecordDynamicMapper {

	BasicColumn[] selectList = BasicColumn.columnList(
			id, billDate, billType, downloadUrl, status, rawResponse, createTime, updateTime
	);

	@InsertProvider(type = SqlProviderAdapter.class, method = "insert")
	@Options(useGeneratedKeys = true, keyProperty = "row.id")
	int insert(InsertStatementProvider<PaymentReconcileRecordData> insertStatement);

	@UpdateProvider(type = SqlProviderAdapter.class, method = "update")
	int updateStatement(UpdateStatementProvider updateStatement);

	@DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
	int deleteStatement(DeleteStatementProvider deleteStatement);

	@SelectProvider(type = SqlProviderAdapter.class, method = "select")
	@Results(id = "PaymentReconcileRecordDataResult", value = {
			@Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "bill_date", property = "billDate", jdbcType = JdbcType.DATE),
			@Result(column = "bill_type", property = "billType", jdbcType = JdbcType.VARCHAR),
			@Result(column = "download_url", property = "downloadUrl", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "status", property = "status", jdbcType = JdbcType.VARCHAR),
			@Result(column = "raw_response", property = "rawResponse", jdbcType = JdbcType.LONGVARCHAR),
			@Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE),
			@Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
	})
	List<PaymentReconcileRecordData> selectMany(SelectStatementProvider selectStatement);

	default int create(PaymentReconcileRecordData data) {
		return MyBatis3Utils.insert(this::insert, data, paymentReconcileRecord, c -> c
				.map(billDate).toProperty("billDate")
				.map(billType).toProperty("billType")
				.map(downloadUrl).toPropertyWhenPresent("downloadUrl", data::downloadUrl)
				.map(status).toProperty("status")
				.map(rawResponse).toPropertyWhenPresent("rawResponse", data::rawResponse)
				.map(createTime).toPropertyWhenPresent("createTime", data::createTime)
				.map(updateTime).toPropertyWhenPresent("updateTime", data::updateTime)
		);
	}

	default int update(PaymentReconcileRecordData data, PaymentReconcileRecordQueryOptions options) {
		PaymentReconcileRecordQueryOptions safeOptions = options == null ? PaymentReconcileRecordQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment reconcile update requires conditions");
		}
		return MyBatis3Utils.update(this::updateStatement, paymentReconcileRecord, c -> c
				.set(downloadUrl).equalToWhenPresent(data::downloadUrl)
				.set(status).equalToWhenPresent(data::status)
				.set(rawResponse).equalToWhenPresent(data::rawResponse)
				.set(updateTime).equalToConstant("NOW()")
				.where(billDate, isEqualToWhenPresent(safeOptions::getBillDateValue))
				.and(billType, isEqualToWhenPresent(safeOptions::getBillTypeValue))
		);
	}

	default int delete(PaymentReconcileRecordQueryOptions options) {
		PaymentReconcileRecordQueryOptions safeOptions = options == null ? PaymentReconcileRecordQueryOptions.none() : options;
		if (!safeOptions.hasConditions()) {
			throw new IllegalArgumentException("payment reconcile delete requires conditions");
		}
		return MyBatis3Utils.deleteFrom(this::deleteStatement, paymentReconcileRecord, c -> c
				.where(billDate, isEqualToWhenPresent(safeOptions::getBillDateValue))
				.and(billType, isEqualToWhenPresent(safeOptions::getBillTypeValue))
		);
	}

	default List<PaymentReconcileRecordData> query(PaymentReconcileRecordQueryOptions options) {
		PaymentReconcileRecordQueryOptions safeOptions = options == null ? PaymentReconcileRecordQueryOptions.none() : options;
		return MyBatis3Utils.selectList(this::selectMany, selectList, paymentReconcileRecord, c -> c
				.where(billDate, isEqualToWhenPresent(safeOptions::getBillDateValue))
				.and(billType, isEqualToWhenPresent(safeOptions::getBillTypeValue))
				.orderBy(billDate)
		);
	}
}

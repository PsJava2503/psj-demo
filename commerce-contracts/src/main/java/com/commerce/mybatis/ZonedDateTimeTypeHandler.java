package com.commerce.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(ZonedDateTime.class)
@MappedJdbcTypes(value = JdbcType.TIMESTAMP_WITH_TIMEZONE, includeNullJdbcType = true)
public class ZonedDateTimeTypeHandler extends BaseTypeHandler<ZonedDateTime> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, ZonedDateTime parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setObject(i, parameter.toOffsetDateTime(), Types.TIMESTAMP_WITH_TIMEZONE);
	}

	@Override
	public ZonedDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return toZonedDateTime(rs.getObject(columnName, OffsetDateTime.class));
	}

	@Override
	public ZonedDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return toZonedDateTime(rs.getObject(columnIndex, OffsetDateTime.class));
	}

	@Override
	public ZonedDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return toZonedDateTime(cs.getObject(columnIndex, OffsetDateTime.class));
	}

	private ZonedDateTime toZonedDateTime(OffsetDateTime value) {
		return value == null ? null : value.toZonedDateTime();
	}

}

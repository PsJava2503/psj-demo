package com.commerce.order.infrastructure.persistence;

import com.commerce.order.domain.model.OrderRefundRequest;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRefundRequestRepository {

	private final JdbcTemplate jdbcTemplate;

	private final RowMapper<OrderRefundRequest> rowMapper = (rs, rowNum) -> new OrderRefundRequest(
			rs.getLong("id"),
			rs.getString("refund_request_no"),
			rs.getLong("checkout_order_id"),
			rs.getObject("sub_order_id", Long.class),
			rs.getBigDecimal("refund_amount"),
			rs.getString("reason"),
			rs.getString("status"),
			rs.getString("raw_response"),
			rs.getTimestamp("create_time").toInstant().atZone(java.time.ZoneOffset.UTC),
			rs.getTimestamp("update_time").toInstant().atZone(java.time.ZoneOffset.UTC)
	);

	public OrderRefundRequestRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public OrderRefundRequest create(OrderRefundRequest request) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement("""
					INSERT INTO order_refund_requests (
					    refund_request_no, checkout_order_id, sub_order_id, refund_amount, reason, status, raw_response
					) VALUES (?, ?, ?, ?, ?, ?, ?)
					""", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, request.refundRequestNo());
			ps.setLong(2, request.checkoutOrderId());
			ps.setObject(3, request.subOrderId());
			ps.setBigDecimal(4, request.refundAmount());
			ps.setString(5, request.reason());
			ps.setString(6, request.status());
			ps.setString(7, request.rawResponse());
			return ps;
		}, keyHolder);
		Number key = keyHolder.getKey();
		return new OrderRefundRequest(
				key == null ? null : key.longValue(),
				request.refundRequestNo(),
				request.checkoutOrderId(),
				request.subOrderId(),
				request.refundAmount(),
				request.reason(),
				request.status(),
				request.rawResponse(),
				request.createTime(),
				request.updateTime()
		);
	}

	public Optional<OrderRefundRequest> findByRefundRequestNo(String refundRequestNo) {
		List<OrderRefundRequest> rows = jdbcTemplate.query("""
				SELECT id, refund_request_no, checkout_order_id, sub_order_id, refund_amount, reason, status,
				    raw_response, create_time, update_time
				FROM order_refund_requests
				WHERE refund_request_no = ?
				""", rowMapper, refundRequestNo);
		return rows.stream().findFirst();
	}

	public int updateStatus(String refundRequestNo, String status, String rawResponse) {
		return jdbcTemplate.update("""
				UPDATE order_refund_requests
				SET status = ?, raw_response = ?, update_time = NOW()
				WHERE refund_request_no = ?
				""", status, rawResponse, refundRequestNo);
	}
}

package com.commerce.order.infrastructure.persistence;

import com.commerce.order.domain.model.OrderSubOrder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OrderSubOrderRepository {

	private final JdbcTemplate jdbcTemplate;

	private final RowMapper<OrderSubOrder> rowMapper = (rs, rowNum) -> new OrderSubOrder(
			rs.getLong("id"),
			rs.getLong("checkout_order_id"),
			rs.getLong("product_id"),
			rs.getObject("sku_id", Long.class),
			rs.getString("product_name"),
			rs.getBigDecimal("unit_price"),
			rs.getInt("quantity"),
			rs.getBigDecimal("amount"),
			rs.getObject("merchant_id", Long.class),
			rs.getString("status"),
			rs.getString("refund_status"),
			rs.getTimestamp("create_time").toInstant().atZone(java.time.ZoneOffset.UTC),
			rs.getTimestamp("update_time").toInstant().atZone(java.time.ZoneOffset.UTC)
	);

	public OrderSubOrderRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public OrderSubOrder create(OrderSubOrder subOrder) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement("""
					INSERT INTO order_sub_orders (
					    checkout_order_id, product_id, sku_id, product_name, unit_price, quantity, amount,
					    merchant_id, status, refund_status
					) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
					""", Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, subOrder.checkoutOrderId());
			ps.setLong(2, subOrder.productId());
			ps.setObject(3, subOrder.skuId());
			ps.setString(4, subOrder.productName());
			ps.setBigDecimal(5, subOrder.unitPrice());
			ps.setInt(6, subOrder.quantity());
			ps.setBigDecimal(7, subOrder.amount());
			ps.setObject(8, subOrder.merchantId());
			ps.setString(9, subOrder.status());
			ps.setString(10, subOrder.refundStatus());
			return ps;
		}, keyHolder);
		Number key = keyHolder.getKey();
		return new OrderSubOrder(
				key == null ? null : key.longValue(),
				subOrder.checkoutOrderId(),
				subOrder.productId(),
				subOrder.skuId(),
				subOrder.productName(),
				subOrder.unitPrice(),
				subOrder.quantity(),
				subOrder.amount(),
				subOrder.merchantId(),
				subOrder.status(),
				subOrder.refundStatus(),
				subOrder.createTime(),
				subOrder.updateTime()
		);
	}

	public List<OrderSubOrder> findByCheckoutOrderId(Long checkoutOrderId) {
		return jdbcTemplate.query("""
				SELECT id, checkout_order_id, product_id, sku_id, product_name, unit_price, quantity, amount,
				    merchant_id, status, refund_status, create_time, update_time
				FROM order_sub_orders
				WHERE checkout_order_id = ?
				ORDER BY id
				""", rowMapper, checkoutOrderId);
	}

	public List<OrderSubOrder> findByCheckoutOrderIdAndStatus(Long checkoutOrderId, String status) {
		return jdbcTemplate.query("""
				SELECT id, checkout_order_id, product_id, sku_id, product_name, unit_price, quantity, amount,
				    merchant_id, status, refund_status, create_time, update_time
				FROM order_sub_orders
				WHERE checkout_order_id = ? AND status = ?
				ORDER BY id
				""", rowMapper, checkoutOrderId, status);
	}

	public int updateStatusForCheckoutOrder(Long checkoutOrderId, String status) {
		return jdbcTemplate.update("""
				UPDATE order_sub_orders
				SET status = ?, update_time = NOW()
				WHERE checkout_order_id = ?
				""", status, checkoutOrderId);
	}

	public int updateRefundStatus(Long subOrderId, String refundStatus) {
		return jdbcTemplate.update("""
				UPDATE order_sub_orders
				SET refund_status = ?, update_time = NOW()
				WHERE id = ?
				""", refundStatus, subOrderId);
	}
}

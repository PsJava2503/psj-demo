package com.commerce.payment.application.service.reconcile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AlipayBillParser {

	public List<PaymentBillLine> parse(String content) {
		if (!StringUtils.hasText(content)) {
			return List.of();
		}
		List<String> lines = content.lines()
				.map(String::trim)
				.filter(StringUtils::hasText)
				.filter(line -> !line.startsWith("#"))
				.toList();
		if (lines.isEmpty()) {
			return List.of();
		}
		String[] header = split(lines.get(0));
		Map<String, Integer> indexes = indexes(header);
		List<PaymentBillLine> result = new ArrayList<>();
		for (int i = 1; i < lines.size(); i++) {
			String rawLine = lines.get(i);
			String[] columns = split(rawLine);
			String outTradeNo = value(columns, indexes, "out_trade_no", "商户订单号");
			String outRefundNo = value(columns, indexes, "out_refund_no", "out_request_no", "退款请求号");
			if (!StringUtils.hasText(outTradeNo) && !StringUtils.hasText(outRefundNo)) {
				continue;
			}
			BigDecimal amount = amount(value(columns, indexes, "amount", "total_amount", "refund_amount", "订单金额", "退款金额"));
			result.add(new PaymentBillLine(
					outTradeNo,
					value(columns, indexes, "trade_no", "支付宝交易号"),
					outRefundNo,
					amount,
					value(columns, indexes, "trade_status", "交易状态"),
					rawLine
			));
		}
		return result;
	}

	private String[] split(String line) {
		return line.split(",", -1);
	}

	private Map<String, Integer> indexes(String[] header) {
		Map<String, Integer> indexes = new HashMap<>();
		for (int i = 0; i < header.length; i++) {
			indexes.put(header[i].trim(), i);
		}
		return indexes;
	}

	private String value(String[] columns, Map<String, Integer> indexes, String... keys) {
		for (String key : keys) {
			Integer index = indexes.get(key);
			if (index != null && index < columns.length) {
				return columns[index].trim();
			}
		}
		return "";
	}

	private BigDecimal amount(String value) {
		if (!StringUtils.hasText(value)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(value.trim());
	}
}

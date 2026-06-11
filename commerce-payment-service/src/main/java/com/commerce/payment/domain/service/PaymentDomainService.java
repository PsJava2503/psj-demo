package com.commerce.payment.domain.service;

import com.commerce.payment.domain.model.PaymentOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PaymentDomainService {

	private static final DateTimeFormatter TRADE_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	public void validateRequest(Long orderId, BigDecimal amount) {
		if (orderId == null || amount == null || amount.signum() <= 0) {
			throw new IllegalArgumentException("invalid payment request");
		}
	}

	public String defaultSubject(Long orderId) {
		return "order-" + orderId;
	}

	public String generateOutTradeNo(Long orderId) {
		return "T" + orderId + TRADE_NO_TIME.format(LocalDateTime.now());
	}

	public String generateOutRefundNo(Long orderId) {
		return "R" + orderId + TRADE_NO_TIME.format(LocalDateTime.now());
	}

	public PaymentOrderStatus mapTradeStatus(String tradeStatus) {
		if (!StringUtils.hasText(tradeStatus)) {
			return PaymentOrderStatus.WAIT_BUYER_PAY;
		}
		return switch (tradeStatus) {
			case "TRADE_SUCCESS", "TRADE_FINISHED" -> PaymentOrderStatus.TRADE_SUCCESS;
			case "TRADE_CLOSED" -> PaymentOrderStatus.TRADE_CLOSED;
			default -> PaymentOrderStatus.WAIT_BUYER_PAY;
		};
	}

}

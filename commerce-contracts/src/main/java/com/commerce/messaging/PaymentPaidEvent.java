package com.commerce.messaging;

import java.math.BigDecimal;

public record PaymentPaidEvent(Long orderId, String outTradeNo, String tradeNo, BigDecimal amount) {
}

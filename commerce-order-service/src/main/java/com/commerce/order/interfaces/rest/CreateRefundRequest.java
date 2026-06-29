package com.commerce.order.interfaces.rest;

import java.math.BigDecimal;

public record CreateRefundRequest(Long subOrderId, BigDecimal amount, String reason) {
}

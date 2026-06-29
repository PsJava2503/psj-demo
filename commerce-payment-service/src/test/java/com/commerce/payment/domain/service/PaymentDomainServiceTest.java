package com.commerce.payment.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PaymentDomainServiceTest {

	private final PaymentDomainService paymentDomainService = new PaymentDomainService();

	@Test
	void stableRefundNumberIsDerivedFromRefundRequestId() {
		String first = paymentDomainService.generateOutRefundNo("MANUAL-100-200");
		String second = paymentDomainService.generateOutRefundNo("MANUAL-100-200");

		assertThat(first).isEqualTo(second);
		assertThat(first).startsWith("RMANUAL100200");
	}

	@Test
	void longRefundNumberIsTrimmedToGatewayLimit() {
		String outRefundNo = paymentDomainService.generateOutRefundNo("REQUEST-" + "X".repeat(100));

		assertThat(outRefundNo).hasSize(64);
	}
}

package com.commerce.payment.infrastructure.alipay;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(AlipaySandboxProperties.class)
public class AlipayClientConfig {

	@Bean
	public RestTemplate alipayRestTemplate() {
		return new RestTemplate();
	}
}

package com.commerce.payment.infrastructure.mq;

import com.commerce.messaging.MqTopology;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentMqConfig {

	@Bean
	public TopicExchange paymentExchange() {
		return new TopicExchange(MqTopology.PAYMENT_EXCHANGE, true, false);
	}

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}

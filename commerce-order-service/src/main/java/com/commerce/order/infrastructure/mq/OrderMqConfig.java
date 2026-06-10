package com.commerce.order.infrastructure.mq;

import com.commerce.messaging.MqTopology;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMqConfig {

	@Bean
	public TopicExchange orderExchange() {
		return new TopicExchange(MqTopology.ORDER_EXCHANGE, true, false);
	}

	@Bean
	public Queue orderCreatedQueue() {
		return new Queue(MqTopology.ORDER_CREATED_QUEUE, true);
	}

	@Bean
	public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
		return BindingBuilder.bind(orderCreatedQueue)
				.to(orderExchange)
				.with(MqTopology.ORDER_CREATED_ROUTING_KEY);
	}

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}

package com.commerce.order.domain.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class OrderIdGenerator {

	private static final DateTimeFormatter ORDER_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	private long lastMillis;
	private int sequence;

	public synchronized Long nextId() {
		long now = System.currentTimeMillis();
		if (now == lastMillis) {
			sequence = (sequence + 1) % 1000;
			if (sequence == 0) {
				while (System.currentTimeMillis() == now) {
					Thread.onSpinWait();
				}
				now = System.currentTimeMillis();
			}
		}
		else {
			sequence = 0;
		}
		lastMillis = now;
		return now * 1000 + sequence;
	}

	public String nextOrderNo() {
		return "ORD" + ORDER_NO_TIME.format(LocalDateTime.now()) + randomSuffix();
	}

	private String randomSuffix() {
		return String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
	}
}

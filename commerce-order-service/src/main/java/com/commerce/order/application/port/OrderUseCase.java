package com.commerce.order.application.port;

import com.commerce.order.application.command.CreateOrderCommand;

public interface OrderUseCase {

	String create(CreateOrderCommand command);

}

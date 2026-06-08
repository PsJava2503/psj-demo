package com.psj.commerce.order.application.port;

import com.psj.commerce.order.application.command.CreateOrderCommand;

public interface OrderUseCase {

	String create(CreateOrderCommand command);

}

package com.commerce.address;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.commerce.address.infrastructure.persistence.mapper")
public class AddressApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddressApplication.class, args);
	}

}

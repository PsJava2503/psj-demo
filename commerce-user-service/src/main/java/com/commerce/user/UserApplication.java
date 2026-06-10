package com.commerce.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.commerce.user.infrastructure.persistence.mapper")
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}

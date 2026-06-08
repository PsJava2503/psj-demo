package com.example.psjdemo.provider;

import com.example.psjdemo.api.DemoGreetingService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class DemoGreetingServiceImpl implements DemoGreetingService {

	@Override
	public String greet(String name) {
		return "Hello, " + name + "! This response comes from Dubbo provider.";
	}

}

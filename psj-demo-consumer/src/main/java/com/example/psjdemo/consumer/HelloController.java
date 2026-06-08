package com.example.psjdemo.consumer;

import com.example.psjdemo.api.DemoGreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@DubboReference(check = false)
	private DemoGreetingService demoGreetingService;

	@GetMapping("/hello")
	public String hello(@RequestParam(defaultValue = "psj-demo") String name) {
		return demoGreetingService.greet(name);
	}

}

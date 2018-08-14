package com.trs.devops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class DevopsApplication {
	@RequestMapping("/")
	String index() {
		return "Spring Boot-index";
	}
	public static void main(String[] args) {
		SpringApplication.run(DevopsApplication.class, args);
	}
}

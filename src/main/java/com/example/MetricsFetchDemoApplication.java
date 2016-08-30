package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@EnableEurekaClient
public class MetricsFetchDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetricsFetchDemoApplication.class, args);
	}
}

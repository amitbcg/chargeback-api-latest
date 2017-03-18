package com.chargeback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ChargeBackMetricsFetchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChargeBackMetricsFetchApplication.class, args);
	}
}

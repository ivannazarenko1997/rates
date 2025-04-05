package com.echange.api.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ExchangeRateApplication {
	public static void main(String[] args) {
		System.setProperty("spring.threads.virtual.enabled", "true");
		SpringApplication.run(ExchangeRateApplication.class, args);
	}
}
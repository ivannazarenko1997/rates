package com.echange.api.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ExchangeRateApplication {
	private static final Logger log = LoggerFactory.getLogger(ExchangeRateApplication.class);
	public static void main(String[] args) {
		log.info("Starting application exchangerates");

		System.setProperty("spring.threads.virtual.enabled", "true");
	    SpringApplication.run(ExchangeRateApplication.class, args);
	}
}
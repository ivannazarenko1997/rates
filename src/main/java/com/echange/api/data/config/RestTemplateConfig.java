package com.echange.api.data.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRetry
public class RestTemplateConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(2000) // 2 seconds delay between retries
                .retryOn(Exception.class)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // You can customize it further
    }
}

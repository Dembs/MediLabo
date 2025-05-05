package com.medilabo.diabetes_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
 public class AppConfig {
    @Value("${GATEWAY_AUTH_USERNAME}")
    private String backendApiUsername;

    @Value("${GATEWAY_AUTH_PASSWORD}")
    private String backendApiPassword;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(backendApiUsername, backendApiPassword)
        );
        return restTemplate;
    }
 }

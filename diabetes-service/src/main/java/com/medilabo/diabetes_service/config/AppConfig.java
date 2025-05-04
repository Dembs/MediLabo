package com.medilabo.diabetes_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
 public class AppConfig {
    @Value("${backend.api.username}")
    private String backendApiUsername;

    @Value("${backend.api.password}")
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

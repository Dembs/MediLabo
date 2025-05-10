package com.medilabo.diabetes_service.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;


/**
 * Cette classe fournit les beans nécessaires pour le fonctionnement de l'application,
 * notamment les composants permettant la communication avec les services backend.
 */
@Configuration
 public class AppConfig {
    @Value("${GATEWAY_AUTH_USERNAME}")
    private String backendApiUsername;

    @Value("${GATEWAY_AUTH_PASSWORD}")
    private String backendApiPassword;
    /**
     * Crée et configure un Interceptor utilisé par l'application pour
     * effectuer des appels HTTP vers les API des microservices backend.
     *
     * @return Une instance de RestTemplate prête à l'emploi
     */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(backendApiUsername, backendApiPassword);
    }
 }

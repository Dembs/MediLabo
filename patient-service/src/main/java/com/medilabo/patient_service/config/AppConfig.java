package com.medilabo.patient_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Cette classe fournit les beans nécessaires pour le fonctionnement de l'application,
 * notamment les composants permettant la communication avec les autres microservices.
 */
@Configuration
public class AppConfig {

    /**
     * Crée et configure un RestTemplate utilisé par l'application pour
     * effectuer des appels HTTP vers les API des microservices backend.
     *
     * @return Une instance de RestTemplate prête à l'emploi
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
package com.medilabo.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de sécurité pour l'API Gateway.
 * Cette classe définit les paramètres de sécurité pour l'API Gateway qui sert de point d'entrée unique
 * pour toutes les requêtes vers les microservices backend.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${SPRING_SECURITY_USER_NAME}")
    private String backendApiUsername;

    @Value("${SPRING_SECURITY_USER_PASSWORD}")
    private String backendApiPassword;

    /**
     * Définit l'encodeur de mot de passe utilisé pour sécuriser les mots de passe stockés.
     *
     * @return Un encodeur BCrypt pour le hachage des mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure l'user details service.
     * Crée un utilisateur unique en mémoire avec les identifiants définis dans les variables d'environnement.
     *
     * @param passwordEncoder L'encodeur utilisé pour le hachage du mot de passe
     * @return Un service réactif de détails utilisateur avec l'utilisateur configuré
     */
    @Bean
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                               .username(backendApiUsername)
                               .password(passwordEncoder.encode(backendApiPassword))
                               .roles("USER")
                               .build();
        return new MapReactiveUserDetailsService(user);
    }

    /**
     * Configure la filter chain de sécurité pour l'API Gateway.
     * Applique l'authentification HTTP Basic à tous les endpoints
     *
     * @param http L'objet ServerHttpSecurity à configurer
     * @return La chaîne de filtres de sécurité configurée
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }
}
package com.medilabo.front_end.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité de l'application front-end.
 * On définit les règles d'authentification et d'autorisation pour l'application,
 * en utilisant Spring Security pour sécuriser les endpoints et gérer les connexions utilisateur.
 * Utilise une authentification en mémoire avec les mêmes identifiants que ceux utilisés
 * pour communiquer avec les API backend.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${BACKEND_API_USERNAME}")
    private String backendApiUsername;

    @Value("${BACKEND_API_PASSWORD}")
    private String backendApiPassword;

    /**
     * Crée un encodeur de mot de passe pour le stockage sécurisé des mots de passe.
     *
     * @return Un encodeur de mot de passe BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure le service user details en mémoire.
     * Utilise les mêmes identifiants que ceux configurés pour les appels d'API backend.
     *
     * @param passwordEncoder L'encodeur de mot de passe à utiliser
     * @return Un service de détails utilisateur avec l'utilisateur configuré
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                               .username(backendApiUsername)
                               .password(passwordEncoder.encode(backendApiPassword))
                               .roles("USER")
                               .build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Configure la filter chain de sécurité pour définir les règles d'authentification
     * et d'autorisation de l'application.
     *
     * @param http L'objet HttpSecurity à configurer
     * @return La chaîne de filtres de sécurité configurée
     * @throws Exception Si une erreur survient lors de la configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form
                        .defaultSuccessUrl("/ui/patients", false)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
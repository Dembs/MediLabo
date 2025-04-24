package com.medilabo.patient_service.config;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Crée un utilisateur "user" avec un mot de passe "password"
        // Mot de passe encodé avant d'être stocké
        UserDetails user = User.builder()
                               .username("user")
                               .password(passwordEncoder.encode("password"))
                               .roles("USER")
                               .build();

        // Gère les utilisateurs en mémoire
        return new InMemoryUserDetailsManager(user);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(authz -> authz
                        .anyRequest().authenticated()
                )
                // Active l'authentification HTTP Basic
                .httpBasic(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}

package com.needed.task.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.needed.task.jwt.JwtAuthEntryPoint;
import com.needed.task.jwt.JwtAuthFilter;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
     @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8181"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);
                return configuration;
            }))
            .authorizeHttpRequests(authorize -> {
                authorize.requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll();
                
                authorize.requestMatchers("/api/auth/**").permitAll();
                
                authorize.requestMatchers(
                    "/",
                    "/index.html",
                    "/app.js", 
                    "/style.css",
                    "/favicon.ico"
                ).permitAll();
                

                authorize.requestMatchers("/error").permitAll();
                
                
                authorize.anyRequest().authenticated();
            })
            .sessionManagement(session -> session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(jwtAuthEntryPoint))
            .addFilterBefore(jwtAuthFilter, 
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

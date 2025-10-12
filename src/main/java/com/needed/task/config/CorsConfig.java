package com.needed.task.config; // или com.example.demo

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer()
    {   return new WebMvcConfigurer() 
    {
        
        @Override
        public void addCorsMappings(CorsRegistry registry) 
        {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8181", "http://127.0.0.1:8181") // или ваш фронтенд URL
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8181", "http://127.0.0.1:8181")
            .allowedMethods("GET", "POST")
            .allowedHeaders("*");
        }
    };
    }
}
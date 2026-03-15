package com.vti.springdatajpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")  // Cho phép tất cả endpoint
                        .allowedOrigins("http://localhost:5175", "http://localhost:5173", "http://localhost:3000")  // Origin của FE (Vite/React)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true)  // Nếu dùng cookie/JWT với credential
                        .maxAge(3600);  // Cache preflight 1 giờ
            }
        };
    }
}

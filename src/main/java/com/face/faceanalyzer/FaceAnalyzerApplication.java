package com.face.faceanalyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class FaceAnalyzerApplication {

    @Value("${cors.allowed-origins}")
    public String allowedOrigins;

    public static void main(String[] args) {
        SpringApplication.run(FaceAnalyzerApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        log.info("Allowed origins received: {}", allowedOrigins);
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(allowedOrigins);
            }
        };
    }
}

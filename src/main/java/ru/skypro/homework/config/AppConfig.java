package ru.skypro.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AppConfig {

    @Bean
    public Path uploadDir() {
        return Paths.get("uploads/images");
    }
}

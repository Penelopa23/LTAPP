package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * The Main class is the entry point of the application.
 * It configures and starts the Spring Boot application.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
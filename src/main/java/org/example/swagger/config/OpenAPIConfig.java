package org.example.swagger.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * This class is a configuration class for OpenAPI.
 * It generates and returns the OpenAPI object for the Tutorial Management API.
 */
@Configuration
public class OpenAPIConfig {


    /**
     * The devUrl variable represents the URL of the development server in the OpenAPIConfig class.
     * It is retrieved from the application properties using the value of the "x5.openapi.dev-url" key.
     */
    @Value("${x5.openapi.dev-url}")
    private String devUrl;

    /**
     * The prodUrl variable represents the URL of the production server in the OpenAPIConfig class.
     * It is retrieved from the application properties using the value of the "x5.openapi.prod-url" key.
     */
    @Value("${x5.openapi.prod-url}")
    private String prodUrl;

    /**
     * Generates and returns the OpenAPI object for the Tutorial Management API.
     *
     * @return the OpenAPI object containing the API information
     */
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("x5t@gmail.com");
        contact.setName("X5");
        contact.setUrl("https://www.x5.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("LTAPP - Load Testing Application API")
                .version("1.0")
                .contact(contact)
                .description("API for load testing training. Includes authentication, document management, " +
                           "Kafka messaging, and datapool generation. All business endpoints require JWT authentication.")
                .termsOfService("https://www.x5t.com/terms")
                .license(mitLicense);

        // Add JWT security scheme
        Components components = new Components();
        components.addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token obtained from /api/auth/login"));

        // Add security requirement to all endpoints (can be overridden per endpoint)
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer))
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}
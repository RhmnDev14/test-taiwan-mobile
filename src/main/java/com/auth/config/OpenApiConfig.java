package com.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * OpenApiConfig configures the Swagger/OpenAPI documentation metadata
 * and logs the Swagger UI URL once the application has fully started.
 */
@Configuration
public class OpenApiConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenApiConfig.class);

    @Value("${server.port:8080}")
    private int serverPort;

    /**
     * Defines the API metadata shown in the Swagger UI header.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Authentication API")
                        .version("1.0.0")
                        .description("REST API for user registration and login using Email + Password with BCrypt encryption.")
                        .contact(new Contact()
                                .name("Auth Service")
                                .email("admin@example.com")));
    }

    /**
     * Logs the Swagger UI URL to the console after the application is fully ready.
     * This fires after all beans are initialized and the embedded server is up.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUrl() {
        log.info("===========================================================");
        log.info("  Application is running!");
        log.info("  Swagger UI   : http://localhost:{}/swagger-ui/index.html", serverPort);
        log.info("  API Docs     : http://localhost:{}/v3/api-docs", serverPort);
        log.info("===========================================================");
    }
}

package com.example.gaming_directory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean // This bean configures the OpenAPI documentation for the application
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gaming Directory API")
                        .version("1.0.0")
                        .description("API for managing gamers, games, and skill levels"));
    }
}
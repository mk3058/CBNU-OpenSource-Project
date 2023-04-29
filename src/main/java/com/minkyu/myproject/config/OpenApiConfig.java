package com.minkyu.myproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {

    @Bean
    public OpenAPI jwtConfig() {
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("Jwt");

        Components jwtComponents = new Components()
                .addSecuritySchemes("Authorization", jwtScheme);

        return new OpenAPI()
                .components(jwtComponents);
    }
}

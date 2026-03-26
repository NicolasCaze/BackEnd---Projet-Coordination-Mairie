package com.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestion Mairie")
                        .version("1.0.0")
                        .description("API REST pour la gestion des réservations de biens municipaux, " +
                                   "des utilisateurs, des groupes et des pièces justificatives. " +
                                   "Système de coordination pour la mairie.")
                        .contact(new Contact()
                                .name("Équipe Mairie")
                                .email("contact@mairie.fr")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Authentification JWT. Format: Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}

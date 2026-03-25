package com.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mairie Backend API")
                        .version("1.0.0")
                        .description("API pour le système de gestion de réservations de la mairie")
                        .contact(new Contact()
                                .name("Équipe Mairie")
                                .email("contact@mairie.fr")));
    }
}

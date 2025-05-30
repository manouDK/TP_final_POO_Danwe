package com.project.POO.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI eventManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TP final de POO sur la Gestion d'Événements")
                        .description(" gestion d'événements avec stockage JSON")
                        .version("1.0"));
    }
}
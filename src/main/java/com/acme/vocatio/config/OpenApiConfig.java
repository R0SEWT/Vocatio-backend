package com.acme.vocatio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.ExternalDocumentation;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Vocatio")
                        .version("v1.0")
                        .description("API para gestión de usuarios, perfiles y vocaciones")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo Vocatio")
                                .email("vocatio@acme.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación adicional de Vocatio")
                        .url("https://acme.com/vocatio/docs"));
    }
}


// http://localhost:8080/swagger-ui.html
package edu.pe.vallegrande.AuthenticationService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para Spring WebFlux
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Authentication Service API")
                        .description("Microservicio de autenticación reactivo con Spring WebFlux y R2DBC")
                        .version("1.0.0"));
    }
}

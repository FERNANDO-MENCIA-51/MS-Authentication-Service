package edu.pe.vallegrande.AuthenticationService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de CORS para Spring WebFlux
 */
@Configuration
public class CorsConfig {

        @Bean
        public CorsWebFilter corsWebFilter() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();

                // Permitir orígenes específicos (frontend en diferentes puertos)
                corsConfiguration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:5173"

                ));

                // Permitir credenciales (necesario para enviar cookies/auth headers)
                corsConfiguration.setAllowCredentials(true);

                // Métodos HTTP permitidos
                corsConfiguration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));

                // Headers permitidos (incluir todos los que el frontend pueda enviar)
                corsConfiguration.setAllowedHeaders(Arrays.asList(
                                "Origin",
                                "Content-Type",
                                "Accept",
                                "Authorization",
                                "Access-Control-Request-Method",
                                "Access-Control-Request-Headers",
                                "X-Requested-With",
                                "Cache-Control",
                                "X-Auth-Token"));

                // Headers expuestos al cliente (que el frontend puede leer)
                corsConfiguration.setExposedHeaders(Arrays.asList(
                                "Access-Control-Allow-Origin",
                                "Access-Control-Allow-Credentials",
                                "Authorization",
                                "Content-Disposition",
                                "X-Auth-Token"));

                // Tiempo de cache para preflight requests (1 hora)
                corsConfiguration.setMaxAge(3600L);

                // Aplicar configuración a todas las rutas
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfiguration);

                return new CorsWebFilter(source);
        }
}
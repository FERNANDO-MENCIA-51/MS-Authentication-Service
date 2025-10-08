package edu.pe.vallegrande.AuthenticationService.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;

/**
 * Configuración de seguridad para el microservicio
 * Define qué endpoints están protegidos y qué roles pueden acceder
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Deshabilitar CSRF (no necesario para APIs REST con JWT)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                
                // Configurar autorización de endpoints
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints públicos (sin autenticación)
                        .pathMatchers("/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                        .pathMatchers("/swagger-ui/**", "/webjars/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                        .pathMatchers("/actuator/health").permitAll()
                        
                        // Endpoints de Users - Requieren autenticación
                        .pathMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("ADMIN", "USER_MANAGER", "VIEWER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/users").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        
                        // Endpoints de Persons - Requieren autenticación
                        .pathMatchers(HttpMethod.GET, "/api/v1/persons/**").hasAnyRole("ADMIN", "USER_MANAGER", "VIEWER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/persons").hasAnyRole("SUPER_ADMIN", "ADMIN", "USER_MANAGER")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/persons/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "USER_MANAGER")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/persons/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        
                        // Endpoints de Roles - Solo SUPER_ADMIN
                        .pathMatchers(HttpMethod.GET, "/api/v1/roles/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "VIEWER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/roles").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/roles/**").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/roles/**").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/roles/**").hasRole("SUPER_ADMIN")
                        
                        // Endpoints de Permissions - Solo SUPER_ADMIN
                        .pathMatchers(HttpMethod.GET, "/api/v1/permissions/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "VIEWER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/permissions").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/permissions/**").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/permissions/**").hasRole("SUPER_ADMIN")
                        
                        // Endpoints de Assignments - SUPER_ADMIN y ADMIN
                        .pathMatchers("/api/v1/assignments/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        
                        // Logout requiere estar autenticado
                        .pathMatchers("/api/v1/auth/logout").authenticated()
                        
                        // Cualquier otro endpoint requiere autenticación
                        .anyExchange().authenticated()
                )
                
                // Manejo de errores de autenticación
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        })
                )
                
                // Agregar filtro JWT
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                
                .build();
    }

    /**
     * Bean para encriptar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

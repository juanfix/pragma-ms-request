package co.com.pragma.jjwtsecurity;

import co.com.pragma.jjwtsecurity.jwt.filter.JwtFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity

public class SecurityConfig {
    private final SecurityContextRepository securityContextRepository;
    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

    public SecurityConfig(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, JwtFilter jwtFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchangeSpec ->
                        exchangeSpec
                                .pathMatchers(HttpMethod.POST, "api/v1/login/**").permitAll()
                                .pathMatchers(HttpMethod.GET,
                                        "/webjars/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/v2/api-docs/**",
                                        "/swagger-resources/**",
                                        "/configuration/**"
                                ).permitAll()
                                .anyExchange().authenticated())
                .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.FIRST)
                .securityContextRepository(securityContextRepository)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .exceptionHandling(ex -> ex
                        // 🔑 Cuando no hay autenticación (token inválido, no enviado, etc.)
                        .authenticationEntryPoint(this::authenticationEntryPoint)
                        // 🔑 Cuando hay autenticación, pero no tienes permisos
                        .accessDeniedHandler(this::accessDeniedHandler)
                )
                .build();
    }

    private Mono<Void> authenticationEntryPoint(ServerWebExchange exchange, AuthenticationException ex) {
        logger.severe(ex.getMessage());
        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    private Mono<Void> accessDeniedHandler(ServerWebExchange exchange, AccessDeniedException ex) {
        logger.severe(ex.getMessage());
        return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, ex.getMessage());
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"timestamp\": %s, \"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                LocalDateTime.now().toString(), status.value(), status.getReasonPhrase(), message
        );

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}

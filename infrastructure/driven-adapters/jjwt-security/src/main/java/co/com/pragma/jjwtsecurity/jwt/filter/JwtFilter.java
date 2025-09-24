package co.com.pragma.jjwtsecurity.jwt.filter;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Component
public class JwtFilter implements WebFilter {
    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();
        // Excluir rutas públicas
        if (path.contains("/login")
                || ((path.contains("/webjars")
                || path.contains("/swagger-ui")
                || path.contains("/api-docs")
                || path.contains("/swagger-resources")
                || path.contains("/actuator")
        ) && method == HttpMethod.GET))
            return chain.filter(exchange);
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(auth == null){
            logger.severe("No token was found");
            return unauthorizedError(exchange, HttpStatus.UNAUTHORIZED, "No token was found");
        }
        if(!auth.startsWith("Bearer ")) {
            logger.severe("Invalid token format");
            return unauthorizedError(exchange, HttpStatus.UNAUTHORIZED, "Invalid token format");
        }

        String token = auth.replace("Bearer ", "");
        exchange.getAttributes().put("token", token);
        return chain.filter(exchange).onErrorResume(BadCredentialsException.class, e -> {
            logger.severe(e.getMessage());
            return unauthorizedError(exchange, HttpStatus.UNAUTHORIZED, e.getMessage());
        });
    }

    private Mono<Void> unauthorizedError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"timestamp\": %s, \"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                LocalDateTime.now().toString(), status.value(), status.getReasonPhrase(), message
        );

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}

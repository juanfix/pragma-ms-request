package co.com.pragma.webclient.helpers;

import co.com.pragma.webclient.dto.RoleRequestDTO;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {
    private final Key key;
    private final Long expirationTime;

    public JwtProvider(MicroserviceProps props) {
        this.key = Keys.hmacShaKeyFor(props.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
        this.expirationTime = props.getJwtExpiration();
    }

    public Mono<String> generateToken() {
        return Mono.fromSupplier(() ->
                Jwts.builder()
                        .subject("ms-request")
                        .claim("roles", List.of("ADMIN"))
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + expirationTime))
                        .signWith(key)
                        .compact()
        );
    }
}

package co.com.pragma.webclient.helpers;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

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
                        .setIssuer("ms-request")
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact()
        );
    }
}

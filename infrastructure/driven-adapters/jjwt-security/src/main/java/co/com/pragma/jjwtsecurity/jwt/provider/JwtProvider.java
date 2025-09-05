package co.com.pragma.jjwtsecurity.jwt.provider;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {
    private final Key key;
    private final Long expirationTime;

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

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

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Jwts.parser()
                        .verifyWith((SecretKey) key)
                        .build()
                        .parse(token)
                        .getPayload();
                return true;
            } catch (ExpiredJwtException e) {
                log.error("token expired");
            } catch (UnsupportedJwtException e) {
                log.error("token unsupported");
            } catch (MalformedJwtException e) {
                log.error("token malformed");
            } catch (SignatureException e) {
                log.error("bad signature");
            } catch (IllegalArgumentException e) {
                log.error("illegal args");
            }
            return false;
        });
    }
}

package co.com.pragma.jjwtsecurity.jwt.manager;

import co.com.pragma.jjwtsecurity.jwt.provider.JwtProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtProvider jwtProvider;

    public JwtAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication == null || authentication.getCredentials() == null) {
            // No hay token → no autenticamos → Spring decidirá si la ruta es pública
            return Mono.empty();
        }

        String token = authentication.getCredentials().toString();

        if (token.isBlank()) {
            return Mono.empty(); // también consideramos vacío como "no hay auth"
        }

        return Mono.just(token)
                .map(jwtProvider::getClaims)
                .onErrorResume(e -> Mono.error(new BadCredentialsException("Bad or malformed token", e)))
                .map(claims -> {
                    String username = claims.getSubject();
                    Object rolesClaim = claims.get("roles");

                    List<SimpleGrantedAuthority> authorities = extractAuthorities(rolesClaim);

                    return new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );
                });
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Object rolesClaim) {
        if (rolesClaim == null) {
            return List.of(new SimpleGrantedAuthority("CLIENTE"));
        }

        // Si es una lista de Strings
        if (rolesClaim instanceof List) {
            try {
                return ((List<?>) rolesClaim).stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString) // Convertir cada elemento a String
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error processing roles list: " + e.getMessage());
                return List.of(new SimpleGrantedAuthority("CLIENTE"));
            }
        }

        // Si es un String individual
        if (rolesClaim instanceof String) {
            String role = (String) rolesClaim;
            return List.of(new SimpleGrantedAuthority(role));
        }

        // Para cualquier otro tipo, convertirlo a String
        String role = rolesClaim.toString();
        return List.of(new SimpleGrantedAuthority(role));
    }

}

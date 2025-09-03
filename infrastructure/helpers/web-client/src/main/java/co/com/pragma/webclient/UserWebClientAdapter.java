package co.com.pragma.webclient;

import co.com.pragma.usecase.requests.UserUseCaseInterface;
import co.com.pragma.webclient.dto.UserValidationRequestDTO;
import co.com.pragma.webclient.helpers.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class UserWebClientAdapter implements UserUseCaseInterface {

    private final WebClient webClient;
    private final JwtProvider jwtProvider;
    private static final Logger logger = Logger.getLogger(UserWebClientAdapter.class.getName());

    @Override
    public Mono<Boolean> isValidUser(String identityNumber, String email) {
        logger.info("UserWebClient: Validating user with identity number" + identityNumber + " and email=" + email);
        return jwtProvider.generateToken()
                .flatMap(jwtToken ->
                {
                    return webClient.post()
                            .uri("/api/v1/user/validate")
                            .header("Authorization", "Bearer " + jwtToken)
                            .bodyValue(new UserValidationRequestDTO(identityNumber, email))
                            .exchangeToMono(response -> {
                                if (response.statusCode().is2xxSuccessful()) {
                                    return response.bodyToMono(Boolean.class)
                                            .doOnNext(valid -> logger.info("✅ User authorized: " + valid));
                                } else if (response.statusCode().is4xxClientError()) {
                                    return response.bodyToMono(Boolean.class)
                                            .defaultIfEmpty(false)
                                            .doOnNext(valid -> logger.info("❌ User unauthorized: " + valid));
                                } else {
                                    return Mono.just(false);
                                }
                            });
                })
                .onErrorResume(ex -> {
                    logger.info("WebClient Error: " + ex.getMessage());
                    return Mono.just(false);
                });
    }
}

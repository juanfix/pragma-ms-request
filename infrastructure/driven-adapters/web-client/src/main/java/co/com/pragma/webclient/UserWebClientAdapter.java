package co.com.pragma.webclient;

import co.com.pragma.jjwtsecurity.jwt.provider.JwtProvider;
import co.com.pragma.usecase.requests.interfaces.UserUseCaseInterface;
import co.com.pragma.usecase.requests.dto.UserSalaryInformationDTO;
import co.com.pragma.webclient.dto.UserValidationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class UserWebClientAdapter implements UserUseCaseInterface {

    private final WebClient webClient;
    private final JwtProvider jwtProvider;
    private static final Logger logger = Logger.getLogger(UserWebClientAdapter.class.getName());

    public UserWebClientAdapter(@Qualifier("userWebClient") WebClient webClient, JwtProvider jwtProvider) {
        this.webClient = webClient;
        this.jwtProvider = jwtProvider;
    }

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

    @Override
    public Mono<UserSalaryInformationDTO> getUserSalaryInformation(String identityNumber) {
        logger.info("UserWebClient: Searching salary information for the user email=" + identityNumber);
        return jwtProvider.generateToken()
                .flatMap(jwtToken ->
                {
                    return webClient.get()
                            .uri("/api/v1/user/" + identityNumber)
                            .header("Authorization", "Bearer " + jwtToken)
                            .exchangeToMono(response -> {
                                if (response.statusCode().is2xxSuccessful()) {
                                    return response.bodyToMono(UserSalaryInformationDTO.class)
                                            .doOnNext(valid -> logger.info("✅ User found: " + valid));
                                } else {
                                    return response.bodyToMono(UserSalaryInformationDTO.class)
                                            .defaultIfEmpty(new UserSalaryInformationDTO("Not found", 0L))
                                            .doOnNext(valid -> logger.info("❌ User not found: " + valid));
                                }
                            });
                })
                .onErrorResume(ex -> {
                    logger.info("WebClient Error: " + ex.getMessage());
                    return Mono.just(new UserSalaryInformationDTO("Not found", 0L));
                });
    }
}

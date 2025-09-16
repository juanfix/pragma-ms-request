package co.com.pragma.webclient;

import co.com.pragma.usecase.requests.dto.LambdaDebtCapacityRequestDTO;
import co.com.pragma.usecase.requests.interfaces.LambdaUseCaseInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class LambdaWebClientAdapter implements LambdaUseCaseInterface {

    private final WebClient webClient;
    private static final Logger logger = Logger.getLogger(LambdaWebClientAdapter.class.getName());

    public LambdaWebClientAdapter(@Qualifier("lambdaWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Void> sendToLambdaDebtCapacity(LambdaDebtCapacityRequestDTO lambdaDebtCapacityRequestDTO) {
        logger.info("UserWebClient: Sending petition to debt capacity Lambda=" + lambdaDebtCapacityRequestDTO);
        return webClient.post()
                .uri("/api/v1/calcular-capacidad")
                .bodyValue(lambdaDebtCapacityRequestDTO)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(ex -> {
                    logger.info("WebClient Error: " + ex.getMessage());
                    return Mono.empty();
                });
    }
}

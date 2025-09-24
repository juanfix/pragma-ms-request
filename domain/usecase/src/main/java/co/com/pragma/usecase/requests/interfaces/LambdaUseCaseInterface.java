package co.com.pragma.usecase.requests.interfaces;

import co.com.pragma.usecase.requests.dto.LambdaDebtCapacityRequestDTO;
import reactor.core.publisher.Mono;

public interface LambdaUseCaseInterface {
    Mono<Void> sendToLambdaDebtCapacity(LambdaDebtCapacityRequestDTO lambdaDebtCapacityRequestDTO);
}

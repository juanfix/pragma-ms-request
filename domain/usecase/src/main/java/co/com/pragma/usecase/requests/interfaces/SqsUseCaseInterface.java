package co.com.pragma.usecase.requests.interfaces;

import co.com.pragma.usecase.requests.dto.SqsMessageDTO;
import reactor.core.publisher.Mono;

public interface SqsUseCaseInterface {
    Mono<Void> publishStatusRequest(SqsMessageDTO sqsMessageDTO);
}

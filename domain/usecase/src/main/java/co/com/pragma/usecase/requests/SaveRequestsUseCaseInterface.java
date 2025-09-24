package co.com.pragma.usecase.requests;

import co.com.pragma.model.requests.Requests;
import reactor.core.publisher.Mono;

public interface SaveRequestsUseCaseInterface {
    public Mono<Requests> execute(Requests requests);
}

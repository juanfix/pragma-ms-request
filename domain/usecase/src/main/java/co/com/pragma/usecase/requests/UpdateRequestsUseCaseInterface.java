package co.com.pragma.usecase.requests;

import co.com.pragma.model.requests.Requests;
import reactor.core.publisher.Mono;

public interface UpdateRequestsUseCaseInterface {
    public Mono<Requests> execute(Long requestsId, Long newStatusId);
}

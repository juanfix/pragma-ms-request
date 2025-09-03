package co.com.pragma.usecase.requests;

import co.com.pragma.model.requests.Requests;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RequestsUseCaseInterface {
    public Mono<Requests> saveRequests(Requests requests);
    public Flux<Requests> findAllRequests();
    public Mono<Requests> getRequestsByIdentityNumber(String identityNumber);
    public Mono<Requests> getRequestsByEmail(String email);
}

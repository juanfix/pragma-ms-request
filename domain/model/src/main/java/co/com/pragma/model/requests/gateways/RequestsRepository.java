package co.com.pragma.model.requests.gateways;

import co.com.pragma.model.requests.Requests;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RequestsRepository {
    Mono<Requests> saveRequests(Requests requests);
    Flux<Requests> findAllRequests();
    Mono<Requests> findByIdentityNumber(String identityNumber);
    Mono<Requests> findByEmail(String email);
}

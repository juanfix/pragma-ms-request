package co.com.pragma.model.status.gateways;

import co.com.pragma.model.status.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StatusRepository {
    Flux<Status> findAllStatus();
    Mono<Status> findStatusById(Long id);
}

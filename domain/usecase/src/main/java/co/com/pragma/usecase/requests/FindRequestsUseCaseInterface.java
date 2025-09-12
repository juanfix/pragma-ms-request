package co.com.pragma.usecase.requests;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.dto.PageCriteria;
import co.com.pragma.model.requests.dto.PagedSummary;
import co.com.pragma.model.requests.dto.RequestsFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindRequestsUseCaseInterface {
    public Flux<Requests> findAllRequests();
    public Mono<Requests> findRequestsById(Long id);
    public Mono<PagedSummary<Requests>> findAllRequestsWithSummary(RequestsFilter filter, PageCriteria page);
    public Mono<Requests> getRequestsByIdentityNumber(String identityNumber);
    public Mono<Requests> getRequestsByEmail(String email);
}

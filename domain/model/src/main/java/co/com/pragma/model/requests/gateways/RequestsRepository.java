package co.com.pragma.model.requests.gateways;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.dto.PageCriteria;
import co.com.pragma.model.requests.dto.PagedSummary;
import co.com.pragma.model.requests.dto.RequestsFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RequestsRepository {
    Mono<Requests> saveRequests(Requests requests, Boolean isUpdateRequests);
    Flux<Requests> findAllRequests();
    Flux<Requests> findAllRequestsByEmailAndStatus(String email, Long statusId);
    Mono<Requests> findRequestsById(Long id);
    Mono<Requests> findByIdentityNumber(String identityNumber);
    Mono<Requests> findByEmail(String email);
    Mono<PagedSummary<Requests>> findAllByFilters(RequestsFilter filter, PageCriteria page);
    Mono<Long> countByFilters(RequestsFilter filter);
    Mono<Double> sumAmountByFilters(RequestsFilter filter);
}

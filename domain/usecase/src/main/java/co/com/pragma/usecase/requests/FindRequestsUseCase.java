package co.com.pragma.usecase.requests;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.dto.PageCriteria;
import co.com.pragma.model.requests.dto.PagedSummary;
import co.com.pragma.model.requests.dto.RequestsFilter;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class FindRequestsUseCase implements FindRequestsUseCaseInterface {
    private static final Logger logger = Logger.getLogger(FindRequestsUseCase.class.getName());

    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final RequestsRepository requestsRepository;

    @Override
    public Flux<Requests> findAllRequests() {
        return requestsRepository.findAllRequests();
    }

    @Override
    public Mono<Requests> findRequestsById(Long id) {
        return requestsRepository.findRequestsById(id);
    }

    @Override
    public Mono<PagedSummary<Requests>> findAllRequestsWithSummary(RequestsFilter filter, PageCriteria page) {
        return requestsRepository.findAllByFilters(filter, page)
                .flatMap(pageSummary ->
                        Flux.fromIterable(pageSummary.request())
                                .flatMap(requestDocument -> {
                                    return getRequestsInfoMono(requestDocument);
                                })
                                .collectList()
                                .map(requestsList -> new PagedSummary<>(
                                        requestsList,
                                        pageSummary.page(),
                                        pageSummary.size(),
                                        pageSummary.total()
                                ))
                );
    }

    @Override
    public Mono<Requests> getRequestsByIdentityNumber(String identityNumber) {
        return requestsRepository.findByIdentityNumber(identityNumber);
    }

    @Override
    public Mono<Requests> getRequestsByEmail(String email) {
        return requestsRepository.findByEmail(email);
    }

    private Mono<Requests> getRequestsInfoMono(Requests requestDocument) {
        Mono<Status> status =
                statusRepository.findStatusById(requestDocument.getStatusId());

        Mono<LoanType> loanType =
                loanTypeRepository.findLoanTypeById(requestDocument.getLoanTypeId());

        return Mono.zip(status, loanType)
                .map(tuple -> {
                    requestDocument.setStatusId(tuple.getT1().getId());
                    requestDocument.setLoanTypeId(tuple.getT2().getId());
                    return requestDocument;
                });
    }
}

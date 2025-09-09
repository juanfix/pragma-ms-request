package co.com.pragma.r2dbc;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.dto.PageCriteria;
import co.com.pragma.model.requests.dto.PagedSummary;
import co.com.pragma.model.requests.dto.RequestsFilter;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import co.com.pragma.model.status.Status;
import co.com.pragma.r2dbc.entity.RequestsEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.paginator.Paginator;
import co.com.pragma.usecase.requests.dto.UserSalaryInformationDTO;
import co.com.pragma.webclient.UserWebClientAdapter;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RequestsReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Requests/* change for domain model */,
    RequestsEntity/* change for adapter model */,
    Long,
    RequestsReactiveRepository
> implements RequestsRepository {

    private static final Logger log = LoggerFactory.getLogger(RequestsReactiveRepositoryAdapter.class);
    private final TransactionalOperator transactionalOperator;
    private final Paginator paginator;
    private final UserWebClientAdapter userWebClientAdapter;
    private final StatusReactiveRepositoryAdapter statusReactiveRepositoryAdapter;
    private final LoanTypeReactiveRepositoryAdapter loanTypeReactiveRepositoryAdapter;


    public RequestsReactiveRepositoryAdapter(RequestsReactiveRepository repository,
                                             ObjectMapper mapper,
                                             TransactionalOperator transactionalOperator,
                                             Paginator paginator,
                                             UserWebClientAdapter userWebClientAdapter,
                                             StatusReactiveRepositoryAdapter statusReactiveRepositoryAdapter,
                                             LoanTypeReactiveRepositoryAdapter loanTypeReactiveRepositoryAdapter

    ) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Requests.class));
        this.transactionalOperator = transactionalOperator;
        this.paginator = paginator;
        this.userWebClientAdapter = userWebClientAdapter;
        this.statusReactiveRepositoryAdapter = statusReactiveRepositoryAdapter;
        this.loanTypeReactiveRepositoryAdapter = loanTypeReactiveRepositoryAdapter;
    }

    @Override
    @Transactional
    public Mono<Requests> saveRequests(Requests requests) {
        return transactionalOperator.execute(status -> super.save(requests)).next();
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Requests> findAllRequests() {
        return transactionalOperator.execute(status -> super.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Requests> findByIdentityNumber(String identityNumber) {
        Requests requests = new Requests();
        requests.setIdentityNumber(identityNumber);

        return transactionalOperator.execute(status -> findByExample(requests)
                .switchIfEmpty(Mono.empty())).next();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Requests> findByEmail(String email) {
        Requests requests = new Requests();
        requests.setEmail(email);

        return transactionalOperator.execute(status -> findByExample(requests)
                .switchIfEmpty(Mono.empty())).next();
    }

    @Override
    public Mono<PagedSummary<Requests>> findAllByFilters(RequestsFilter filter, PageCriteria page) {
        Criteria criteria = Criteria.empty();
        if (filter.statusId() != null) {
            criteria = criteria.and("status_id").is(filter.statusId());
        }

        if (filter.loanTypeId() != null) {
            criteria = criteria.and("loan_type_id").is(filter.loanTypeId());
        }

        return paginator.paginate(criteria, RequestsEntity.class, page)
                .flatMap(pageSummaryEntity ->
                        Flux.fromIterable(pageSummaryEntity.request())
                                .flatMap(reqEntity ->
                                        Mono.zip(
                                                statusReactiveRepositoryAdapter.findById(reqEntity.getStatusId()),
                                                loanTypeReactiveRepositoryAdapter.findById(reqEntity.getLoanTypeId()),
                                                userWebClientAdapter.getUserSalaryInformation(reqEntity.getIdentityNumber())
                                        ).map(tuple -> {
                                            Status status = tuple.getT1();
                                            LoanType loanType = tuple.getT2();
                                            UserSalaryInformationDTO userSalaryInformationDTO = tuple.getT3();

                                            // convertir entity → domain y enriquecer
                                            Requests req = this.toEntity(reqEntity);
                                            req.setStatusName(status.getName());
                                            req.setLoanTypeName(loanType.getName());
                                            req.setName(userSalaryInformationDTO.name());
                                            req.setBaseSalary(userSalaryInformationDTO.baseSalary());
                                            req.setMonthlyAmount((req.getAmount() + (req.getAmount() * (loanType.getInterestRate() / 100)))/req.getTerm());

                                            return req;
                                        })
                                )
                                .collectList()
                                .map(content -> new PagedSummary<>(
                                        content,
                                        pageSummaryEntity.page(),
                                        pageSummaryEntity.size(),
                                        pageSummaryEntity.total()
                                ))
                );
    }

    @Override
    public Mono<Long> countByFilters(RequestsFilter filter) {
        return Mono.just(1L);
    }

    @Override
    public Mono<Double> sumAmountByFilters(RequestsFilter filter) {
        return Mono.just(0.0);
    }
}

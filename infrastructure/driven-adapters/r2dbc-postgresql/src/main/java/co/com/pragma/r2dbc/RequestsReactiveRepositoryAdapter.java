package co.com.pragma.r2dbc;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import co.com.pragma.r2dbc.entity.RequestsEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
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

    private final TransactionalOperator transactionalOperator;

    public RequestsReactiveRepositoryAdapter(RequestsReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Requests.class));
        this.transactionalOperator = transactionalOperator;
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
}

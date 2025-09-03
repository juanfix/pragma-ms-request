package co.com.pragma.r2dbc;

import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.r2dbc.entity.StatusEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class StatusReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Status,
        StatusEntity,
        Long,
        StatusReactiveRepository
        > implements StatusRepository {

    private final TransactionalOperator transactionalOperator;

    public StatusReactiveRepositoryAdapter(StatusReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Status.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Flux<Status> findAllStatus() {
        return transactionalOperator.execute(status -> super.findAll());
    }

    @Override
    public Mono<Status> findStatusById(Long id) {
        return transactionalOperator.execute(status -> super.findById(id)).next();
    }
}

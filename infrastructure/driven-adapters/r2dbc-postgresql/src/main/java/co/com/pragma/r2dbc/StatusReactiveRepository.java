package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.StatusEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StatusReactiveRepository extends ReactiveCrudRepository<StatusEntity, Long>, ReactiveQueryByExampleExecutor<StatusEntity> {
}

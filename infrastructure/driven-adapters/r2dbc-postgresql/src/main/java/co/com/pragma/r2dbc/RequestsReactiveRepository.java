package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.RequestsEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface RequestsReactiveRepository extends ReactiveCrudRepository<RequestsEntity, Long>, ReactiveQueryByExampleExecutor<RequestsEntity> {

}

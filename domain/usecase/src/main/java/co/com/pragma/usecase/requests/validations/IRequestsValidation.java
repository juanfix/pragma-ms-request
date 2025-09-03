package co.com.pragma.usecase.requests.validations;

import co.com.pragma.model.requests.Requests;
import reactor.core.publisher.Mono;

public interface IRequestsValidation {
    Mono<Void> validate(Requests requests);
}
